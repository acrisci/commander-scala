package com.github.acrisci.commander

import com.github.acrisci.commander.errors.InvalidCommandException

private class Command(val klass: Class[_], var usage: String, val description: String) {
  var name = ""

  private val methods = klass.getMethods

  if (!methods.exists(_.getName == "main"))
    throw new InvalidCommandException(s"Command ${klass.getName} does not contain a main method")

  private val mainMethod = methods.filter(_.getName == "main").head

  if (mainMethod.getParameterCount != 1 ||
    mainMethod.getParameterTypes.head != classOf[Array[String]] ||
    mainMethod.getReturnType != classOf[Unit]) {
    throw new InvalidCommandException(s"Command ${klass.getName} contains an invalid main method (must be main(args: Array[String]): Unit)")
  }

  if (usage != "") {
    // try determine command name from usage
    val splitUsage = usage.trim.split("\\s+")
    if (splitUsage(0).head.isLetterOrDigit) {
      name = splitUsage(0)
    }
  }

  if (name == "") {
    // if usage did not start with a name, use hyphen-case
    name = toHyphenCase(klass.getName.split("\\.").last)
    usage = s"$name $usage"
  }

  def runMain(args: Array[String]) = {
    // XXX why do I have to nop it or I get NoSuchMethodError?
    def nop(any: Any) = {}

    nop(mainMethod.invoke(klass.newInstance(), args))
  }

  private def toHyphenCase(name: String) = {
    var withHyphens = "[A-Z\\d]".r.replaceAllIn(name, { m =>
      "-" + m.group(0).toLowerCase()
    })

    if (withHyphens.startsWith("-")) {
      withHyphens = withHyphens.drop(1)
    }

    withHyphens
  }
}
