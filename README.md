# Commander Scala

A scalable command-line parser inspired by [commander.js](https://github.com/tj/commander.js).

**Work in progress**

## Option parsing

 Options with commander are defined with the `.option()` method, also serving as documentation for the options. The example below parses args and options from `args`, leaving remaining args as the `program.args` array which were not consumed by options.

```scala
object App {
  def main(args: Array[String]) { 
    var program = new Program()
    program
      .version("0.0.1")
      .option("-p, --peppers", "Add peppers")
      .option("-P, --pineapple", "Add pineapple")
      .option("-b, --bbq-sauce", "Add bbq sauce")
      .option("-c, --cheese [type]", "Add the specified type of cheese [marble]", default="marble")
      .parse(args)

    println("you ordered a pizza with:")
    if (program.peppers) println("  - peppers")
    if (program.pineapple) println("  - pineapple")
    if (program.bbqSauce) println("  - bbq")
    println("  - " + program.cheese + " cheese")
  }
}
```

## Contributing

Make issues on Github to report bugs or suggest new features. Let me know if you are working on something. Right now, I plan on staying as close to commander.js as is practical. However, I will change the api slightly where it makes sense given that Scala is very different from JavaScript. I also might add some features from other argument parsers that I like.

### ToDo

Here are some things that need to be done.

* Git-style commands
* Publish to Maven
* Argument validation
* Test suite
* Scala docs and examples
* Parameterize options to specify type

## License

MIT (see LICENSE)

Copyright © 2015, Tony Crisci
