# Commander Scala

A scalable command-line parser inspired by [commander.js](https://github.com/tj/commander.js).

## Option parsing

 Options with commander are defined with the `.option()` method, also serving as documentation for the options. The example below parses args and options from `args`, leaving remaining args as the `program.args` array which were not consumed by options. Options will be set on the `program` dynamically based on the camelcased form of the long opt.

```scala
import com.github.acrisci.commander.Program

var program = new Program()
  .version("0.0.1")
  .option("-p, --peppers", "Add peppers")
  .option("-P, --pineapple", "Add pineapple")
  .option("-b, --bbq-sauce", "Add bbq sauce")
  .option("-c, --cheese [type]", "Add the specified type of cheese [marble]", default="marble")
  .parse(args)

  if (args.isEmpty)
    program.help

  val peppers = program.peppers.asInstanceOf[Boolean]
  val pineapple = program.pineapple.asInstanceOf[Boolean]
  val bbqSauce = program.bbqSauce.asInstanceOf[Boolean]
  val cheese = program.cheese.asInstanceOf[String]

  println("you ordered a pizza with:")
  if (peppers)
    println("  - peppers")
  if (pineapple)
    println("  - pineapple")
  if (bbqSauce)
    println("  - bbq")
  println("  - " + cheese + " cheese")
```

## Commands

You can define commands on your program like this:

```scala
var program = new Program()
  .version("0.0.1")
  .command(classOf[InstallPackages], "install [packages]", "Install the given packages")
  .command(classOf[SearchPackages], "search [query]", "Search for packages")
  .command(classOf[ListPackages], "list", "List packages")
  .parse(args)
```

If the name of the command is given as the first argument to the program, it will run the `main` method of the given class with the remaining arguments.

## Contributing

`commander-scala` is a work in progress.

Make issues on Github to report bugs or suggest new features. Let me know if you are working on something. Right now, I plan on staying as close to commander.js as is practical. However, I will change the api slightly where it makes sense given that Scala is very different from JavaScript. I also might add some features from other argument parsers that I like.

### To Do

Here are some things that need to be done.

* Publish to Maven (and release)
* Rethink selectDynamic implementation
* Set no help
* Code cleanup
* Test `--help` option and `help` command
* Cleanup program constructor args (debug mode for tests?)
* Promotion
* Command name duplicates

## License

MIT (see LICENSE)

Copyright © 2015, Tony Crisci
