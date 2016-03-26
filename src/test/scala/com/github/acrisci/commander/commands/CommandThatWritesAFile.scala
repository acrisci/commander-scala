package com.github.acrisci.commander.commands

import better.files._

class CommandThatWritesAFile {
  def main(args: Array[String]): Unit = {
    val file = File(args(0))
    file.touch()
  }
}
