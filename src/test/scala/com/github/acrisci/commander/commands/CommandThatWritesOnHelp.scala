package com.github.acrisci.commander.commands

import better.files._

class CommandThatWritesOnHelp {
  def file = "/"/"tmp"/"commander-scala-test"/"implicit-help-flag"
  def main(args: Array[String]): Unit = {
    file.touch()
  }
}
