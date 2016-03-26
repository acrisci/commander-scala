package com.github.acrisci.commander.commands

class CommandWithInvalidMain {
  def main(invalidArg: String): Unit = {
    // main should have arguments of type Array[String]
  }
}
