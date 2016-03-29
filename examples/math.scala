import com.github.acrisci.commander.Program

val program = new Program()
  .version("0.0.1")
  .usage("./run-example.sh math.scala [options]")
  .description("A program that can sum or multiply a list of numbers")
  .option("-o, --operation [operation]", "The operation to perform on the numbers [sum|multiply]", default="sum")
  .option("-n, --numbers <numbers>", "Comma-separated list of numbers", fn=_.split(",").map(_.toInt))
  .parse(args)

if (args.isEmpty)
  program.help()

if (program.operation.equals("sum")) {
  val sum = program.numbers[Array[Int]].sum
  println(s"the sum is $sum")
} else if (program.operation.equals("multiply")) {
  val product = program.numbers[Array[Int]].product
  println(s"the product is $product")
} else {
  println("Operation must be either 'sum' or 'multiply'")
  System.exit(1)
}
