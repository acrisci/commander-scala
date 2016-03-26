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
