import scala.language.dynamics

//class Program(xc: Int, yc: Int) {
class Program() extends Dynamic {
  var version: String = ""
  // list of dynamic properties
  var optionValueMap = Map.empty[String, Any]
  var options: List[Option] = Nil
  var args: List[String] = Nil
  var argv = new Array[String](0)
  var description = ""

  def version(v: String): Program = {
    version = v
    this
  }

  def selectDynamic(name: String) = {
    optionValueMap
      .get(name)
      .getOrElse(sys.error("option '%s' not found".format(name)))
  }

  /*
  def updateDynamic(name: String)(value: Any) {
    map += name -> value
  }
  */

 def option(flags: String, description: String, default: Any = null, fn: String => Any = identity): Program = {
    var opt = new Option(flags, description, fn=fn)

    // register the option
    options = opt :: options

    if (default == null && !opt.takesParam) {
      // default to false instead of null for options without parameters
      optionValueMap = optionValueMap + (camelcase(opt.name) -> false)
    } else {
      optionValueMap = optionValueMap + (camelcase(opt.name) -> default)
    }

    this
  }

  def parse(argv: Array[String]): Program = {
    this.argv = argv
    var normalizedArgs = normalize(argv)
    var lastOpt: Option = null

    outputHelpIfNecessary(normalizedArgs)

    for (i <- 0 to normalizedArgs.length - 1) {
      var arg = normalizedArgs(i)
      var opt = optionFor(arg)

      if (opt != null) {
        if (!opt.takesParam) {
          optionValueMap = optionValueMap + (camelcase(opt.name) -> opt.defaultValue)
        }
      } else if (lastOpt != null) {
        if (lastOpt.takesParam) {
          try {
            optionValueMap = optionValueMap + (camelcase(lastOpt.name) -> lastOpt.fn(arg))
          } catch {
            case e: Exception => {
              exitWithError("Could not parse option: %s".format(lastOpt.name), e)
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

    this
  }

  def normalize(args: Array[String]): Array[String] = {
    var ret :Array[String] = new Array[String](0)
    for (i <- 0 to args.length - 1) {
      var arg = args(i)
      var lastOpt: Option = null

      if (i > 0) {
        lastOpt = optionFor(arg)
      }

      if (arg == "--") {
        // TODO honor option terminator
      } else if (lastOpt != null && lastOpt.required) {
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

  def optionFor(arg: String): Option = {
    for (i <- 0 to options.length - 1) {
      if (options(i).is(arg)) {
        return options(i)
      }
    }

    // XXX option not found?
    return null
  }

  def camelcase(flag: String): String = {
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

  def outputHelpIfNecessary(args: Array[String]) = {
    if (args.contains("--help") || args.contains("-h")) {
      help
    }
  }

  def exitWithError(message: String, e: Exception) = {
    // TODO option to throw an exception here instead of exiting
    println(message)
    sys.exit(1)
  }
}
