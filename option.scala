class Option(var flags: String, var description: String, var fn: String => Any = identity) {
  var required: Boolean = flags.contains("<")
  var optional: Boolean = flags.contains("[")
  var flagsList = splitFlags(flags)
  var short = ""
  var long = ""
  var defaultValue: Any = true

  if (flagsList.length > 1 && !flagsList(1).matches("""^[\[<].*""")) {
    short = flagsList(0)
    long = flagsList(1)
  } else {
    long = flagsList(0)
  }

  def splitFlags(flags: String) :Array[String] = {
    "[ ,|]+".r.split(flags)
  }

  def name() :String = {
    long.replaceAll("--", "").replaceAll("no-", "")
  }

  def is(arg: String): Boolean = {
    return arg == short || arg == long
  }

  def takesParam(): Boolean = required || optional
}
