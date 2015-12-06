class Option(var flags: String, var description: String, var default: Any = null, var required: Boolean = false, var fn: String => Any = identity) {
  var paramRequired: Boolean = flags.contains("<")
  var paramOptional: Boolean = flags.contains("[")
  var flagsList = splitFlags(flags)
  var short = ""
  var long = ""
  var givenParam = false
  var present = false

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

  def takesParam(): Boolean = paramRequired || paramOptional
}
