#!/usr/bin/env bash

program=$(basename $0)
usage="Usage: $program [scala-file] [options]"

if [ $# -lt 1 ]; then
    echo $usage
    exit 0
fi

scala_file=$1

shift

jars_dir=../target/scala-2.11
if [ ! -d $jars_dir ]; then
    echo "Please build the project first with 'sbt package' in the project root"
    exit 1
fi

classpath=../target/scala-2.11/*.jar
classpath=`echo $classpath | tr " " ":"`

scala -cp "$classpath" $scala_file "$@"
