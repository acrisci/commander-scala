#!/usr/bin/env bash

set -e

base="src/main/scala/com/github/acrisci/commander"

mkdir -p docs

scaladoc -d docs $base/Program.scala $base/Opt.scala $base/ProgramParseException.scala
