package com.github.acrisci.commander

import java.io.PrintStream
import java.util

private class Command(val klass: Class[_], var usage: String, val description: String) {
  var name = ""

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
    usage = s"${name} ${usage}"
  }

  def runMain(args: Array[String]) = {
    // XXX why do I have to nop it or I get NoSuchMethodError?
    def nop(any: Any) = {}

    // TODO: handle NoSuchMethodError nicely
    nop(klass.getMethods().filter(_.getName == "main").head.invoke(klass.newInstance(), args))
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
