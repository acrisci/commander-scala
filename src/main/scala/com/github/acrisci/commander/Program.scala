package com.github.acrisci.commander

import scala.language.dynamics

/**
 * The program is a class used to parse command line arguments. Add options to
 * this class with `Program::option()`. Parse arguments you get from the
 * `main()` method with `Program::parse()`. The values of the options will be
 * dynamically set on this object in camelcase form.
 * Example:
 *
 * <pre>
 * object App {
 *   def main(args: Array[String]) {
 *     var program = new Program()
 *       .version("0.0.1")
 *       .option("-p, --peppers", "Add peppers")
 *       .option("-P, --pineapple", "Add pineapple")
 *       .option("-b, --bbq-sauce", "Add bbq sauce")
 *       .option("-c, --cheese [type]", "Add the specified type of cheese [marble]", default="marble")
 *       .parse(args)
 *
 *     println("you ordered a pizza with:")
 *     if (program.peppers) println("  - peppers")
 *     if (program.pineapple) println("  - pineapple")
 *     if (program.bbqSauce) println("  - bbq")
 *     println("  - " + program.cheese + " cheese")
 *   }
 * }
 * </pre>
 *
 * @param exitOnError whether to exit on parse errors or throw a ProgramParseException
 */
class Program(exitOnError: Boolean = true) extends Dynamic {
  /**
   * Unknown arguments not given to an option will be stored here.
   */
  var args: List[String] = Nil

  /**
   * The version of this program. This will be printed when `-v` or `--version`
   * is given on the command line (TODO).
   */
  var version: String = ""

  /**
   * A useful description to be printed in the help string of this program.
   */
  var description = ""

  /**
    * Additional information to be printed at the end of the help string in this program.
    */
  var epilogue = ""

  private var options: List[Opt] = Nil
  private var argv = new Array[String](0)

  /**
   * Set the version of this program.
   *
   * @param v the version of this program
   */
  def version(v: String): Program = {
    version = v
    this
  }

  /**
   * Set a description for this program
   *
   * @param d the description of this program
   */
  def description(d: String): Program= {
    description = d
    this
  }

  /**
    * Set an epilogue message for this program
    *
    * @param e the epilogue message
    */
  def epilogue(e: String): Program = {
    epilogue = e
    this
  }

  /**
   * Options will be set dynamically on this class in camelcase form. Do not
   * use this method directly.
   *
   * @param name the name of the option to get a value for
   */
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

  /**
   * Define option with `flags`, `description` and optional
   * coercion `fn`.
   *
   * The `flags` string should contain both the short and long flags,
   * separated by comma, a pipe or space. The following are all valid
   * all will output this way when `--help` is used.
   *
   * <pre>
   * "-p, --pepper"
   * "-p|--pepper"
   * "-p --pepper"
   * </pre>
   *
   * Examples:
   *
   * <pre>
   * // simple boolean defaulting to false
   * program.option("-p, --pepper", "add pepper")
   * // option with optional parameter defaulting to "marble"
   * program.option("-c, --cheese [type]", "add cheese", default="marble")
   * // option with required parameter
   * program.option("-b, --bbq-sauce <type>", "add bbq sauce")
   * // option that is coerced to an integer
   * program.option("-n, --num [num]", "number of pizzas", default=1, fn=(_.toInt))
   * // option that is required for the program to run
   * program.option("-C, --crust <type>", required=true)
   * </pre>
   *
   * @param flags        The flags for this option (see example)
   * @param description  Description of this option for the help string
   * @param default      The default value when this option is not given
   * @param required     Whether this option is required for the program to run
   * @param fn           Coercion function that takes the string value given on
   *                     the command line and returns the value that will be
   *                     given on the program object
   */
  def option(flags: String, description: String, default: Any = null, required: Boolean = false, fn: String => Any = identity): Program = {
    var opt = new Opt(flags, description, default=default, required=required, fn=fn)

    // register the option
    options = opt :: options

    this
  }

  /**
   * Parse command line args
   *
   * @param argv  Args given to the `main()` method.
   */
  def parse(argv: Array[String]): Program = {
    this.argv = argv
    var normalizedArgs = normalize(argv)
    var lastOpt: Opt = null

    // TODO test me
    outputHelpIfNecessary(normalizedArgs)
    outputVersionIfNecessary(normalizedArgs)

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

  /**
   * Get a help string for this program.
   */
  def helpInformation(): String = {
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

      // add help and version option
      help
        .append("    ")
        .append("-h, --help".padTo(width, " ").mkString)
        .append("  output usage information\n")
        .append("    ")
        .append("-V, --version".padTo(width, " ").mkString)
        .append("  output the version number\n")

        options.foreach((option) => {
          help
            .append("    ")
            .append(option.flags.padTo(width, " ").mkString)
            .append("  ")
            .append(option.description)
            .append("\n")
        })

      // epilogue
      if (epilogue != "") {
        help.append("\n  ")
        help.append(epilogue)
        help.append("\n")
      }

    help.result
  }

  /**
   * Print help information and exit
   */
  def help() = {
    print(helpInformation)
    // XXX throws sbt.TrapExitSecurityException on `sbt run`
    sys.exit(0)
  }

  /**
   * Print version information and exit
   */
  private def outputVersion() = {
    println(version)
    sys.exit(0)
  }

  private def outputHelpIfNecessary(args: Array[String]) = {
    if (args.contains("--help") || args.contains("-h")) {
      help
    }
  }

  private def outputVersionIfNecessary(args: Array[String]) = {
    if (args.contains("--version") || args.contains("-V")) {
      outputVersion
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
