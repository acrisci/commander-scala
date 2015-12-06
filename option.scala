class Option(var flags: String, var description: String, var default: Any = null, var fn: String => Any = identity) {
  var required: Boolean = flags.contains("<")
  var optional: Boolean = flags.contains("[")
  var flagsList = splitFlags(flags)
  var short = ""
  var long = ""
  var givenParam = false

  if (flagsList.length > 1 && !flagsList(1).matches("""^[\[<].*""")) {
    short = flagsList(0)
    long = flagsList(1)
  } else {
    long = flagsList(0)
  }

  var value: Any = null

  if (takesParam || default != null) {
    value = default
  } else {
    value = false
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
