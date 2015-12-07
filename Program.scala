import scala.language.dynamics

class Program(exitOnError: Boolean = true) extends Dynamic {
  var version: String = ""
  private var options: List[Opt] = Nil
  var args: List[String] = Nil
  private var argv = new Array[String](0)
  var description = ""

  def version(v: String): Program = {
    version = v
    this
  }

  def selectDynamic(name: String) = {
    // find the option
    var opt: Opt = null

    for (i <- 0 to options.length - 1) {
      if (camelcase(options(i).name) == name) {
        opt = options(i)
      }
    }

    if (opt == null) {
      sys.error(s"option '$name' not found")
    }

    opt.value
  }

 def option(flags: String, description: String, default: Any = null, required: Boolean = false, fn: String => Any = identity): Program = {
    var opt = new Opt(flags, description, default=default, required=required, fn=fn)

    // register the option
    options = opt :: options

    this
  }

  def parse(argv: Array[String]): Program = {
    this.argv = argv
    var normalizedArgs = normalize(argv)
    var lastOpt: Opt = null

    outputHelpIfNecessary(normalizedArgs)

    for (i <- 0 to normalizedArgs.length - 1) {
      var arg = normalizedArgs(i)
      var opt = optionFor(arg)

      if (opt == null && arg.startsWith("-")) {
        // an unknown option was given
        exitWithError(s"unknown option: $arg")
      }

      if (opt != null) {
        opt.present = true

        if (!opt.takesParam) {
          // XXX I'm not sure how default values should work for booleans yet
          opt.value = (if (opt.default == null) true else opt.default)
        }
      } else if (lastOpt != null) {
        if (lastOpt.takesParam) {
          try {
            lastOpt.givenParam = true
            lastOpt.value = lastOpt.fn(arg)
          } catch {
            case e: Exception => {
              exitWithError(s"Could not parse option: ${lastOpt.name}", e)
            }
          }
        } else {
          args = args :+ arg
        }
      } else {
          args = args :+ arg
      }

      lastOpt = opt
    }

    validateOptions

    this
  }

  private def normalize(args: Array[String]): Array[String] = {
    var ret :Array[String] = new Array[String](0)
    for (i <- 0 to args.length - 1) {
      var arg = args(i)
      var lastOpt: Opt = null

      if (i > 0) {
        lastOpt = optionFor(arg)
      }

      if (arg == "--") {
        // TODO honor option terminator
      } else if (lastOpt != null && lastOpt.paramRequired) {
        ret = ret :+ arg
      } else if (arg.length > 1 && arg.startsWith("-") && '-' != arg(1)) {
        arg.tail.foreach((c) => ret = ret :+ "-" + c)
      } else if (arg.startsWith("--") && arg.contains("=")) {
        var index = arg.indexOf("=")
        ret = ret :+ arg.slice(0, index)
        ret = ret :+ arg.slice(index + 1, arg.length)
      } else {
        ret = ret :+ arg
      }

    }

    return ret
  }

  private def optionFor(arg: String): Opt = {
    for (i <- 0 to options.length - 1) {
      if (options(i).is(arg)) {
        return options(i)
      }
    }

    return null
  }

  private def camelcase(flag: String): String = {
   flag.split('-')
     .filter(_ != "")
     .reduce((str, word) => {
       str + word.capitalize
     })
  }

  def helpString(): String = {
    // XXX is this how you get the file name?
    var programName = new Exception().getStackTrace()(1).getFileName
    if (programName.endsWith(".scala")) {
      programName = programName.take(programName.lastIndexOf("."))
    }

    var help = new StringBuilder()

    // usage information
    help
      .append("\n  Usage: ")
      .append(programName)
      .append(" [options]\n")

    // description
    if (description != "") {
      help
        .append("\n  ")
        .append(description)
        .append("\n")
    }

    // options
    help.append("\n  Options:\n\n")
    var width = options.map(_.flags.length).max

    // add help option
    help
      .append("    ")
      .append("-h, --help".padTo(width, " ").mkString)
      .append("  output usage information\n")

    options.foreach((option) => {
      help
        .append("    ")
        .append(option.flags.padTo(width, " ").mkString)
        .append("  ")
        .append(option.description)
        .append("\n")
    })

    help.result
  }

  def help() = {
    print(helpString)
    // XXX throws sbt.TrapExitSecurityException on `sbt run`
    sys.exit(0)
  }

  private def outputHelpIfNecessary(args: Array[String]) = {
    if (args.contains("--help") || args.contains("-h")) {
      help
    }
  }

  private def validateOptions = {
    options.foreach((o) => {
      if (o.present && o.paramRequired && !o.givenParam) {
        // it was not given a required param
        val message = s"argument missing for ${o.name}"
        exitWithError(message)
      } else if (!o.present && o.required) {
        // option is required and not given
        val message = s"option missing: ${o.name}"
        exitWithError(message)
      }
    })
  }

  private def exitWithError(message: String, e: Exception = null) = {
    if (exitOnError) {
      println(message)
      sys.exit(1)
    } else if (e == null) {
      throw new ProgramParseException(message)
    } else {
      throw e
    }
  }
}
