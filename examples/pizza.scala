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

println("you ordered a pizza with:")
if (program.peppers)
  println("  - peppers")
if (program.pineapple)
  println("  - pineapple")
if (program.bbqSauce)
  println("  - bbq")
println("  - " + program.cheese + " cheese")
