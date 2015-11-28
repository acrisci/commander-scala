import scala.language.dynamics

//class Program(xc: Int, yc: Int) {
class Program() extends Dynamic {
  var version: String = ""
  // list of dynamic properties
  var optionValueMap = Map.empty[String, Any]
  var options: List[Option] = Nil
  var unknownArgs: List[String] = Nil
  var argv = new Array[String](0)

  def version(v: String): Program = {
    version = v
    this
  }

  def selectDynamic(name: String) = {
    optionValueMap get name getOrElse sys.error("option '%s' not found".format(name))
  }

  /*
  def updateDynamic(name: String)(value: Any) {
    map += name -> value
  }
  */

  def option(flags: String, description: String, default: Any = null): Program = {
    var opt = new Option(flags, description)

    // register the option
    options = opt :: options

    var defaultValue: Any = null

    if (default == null && !opt.hasParam) {
      // default to false instead of null for options without parameters
      defaultValue = false
    } else {
      defaultValue = default
    }

    optionValueMap = optionValueMap + (camelcase(opt.name) -> defaultValue)

    this
  }

  def parse(argv: Array[String]): Program = {
    this.argv = argv
    var normalizedArgs = normalize(argv)
    var lastOpt: Option = null

    for (i <- 0 to normalizedArgs.length - 1) {
      var arg = normalizedArgs(i)
      var opt = optionFor(arg)

      if (opt != null) {
        if (!opt.hasParam) {
          optionValueMap = optionValueMap + (camelcase(opt.name) -> opt.defaultValue)
        }
      } else if (lastOpt != null) {
        if (lastOpt.hasParam) {
          optionValueMap = optionValueMap + (camelcase(lastOpt.name) -> arg)
        } else {
          unknownArgs = arg :: unknownArgs
        }
      } else {
          unknownArgs = arg :: unknownArgs
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
}
