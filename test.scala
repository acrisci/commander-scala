import org.scalatest.Assertions._

object Test {
  def testProgram: Program = {
    new Program()
      .version("1.0.0")
      .option("-p, --peppers", "Add peppers")
      .option("-o, --onions", "Add onions")
      .option("-a, --anchovies", "Add anchovies")
      .option("-b, --bbq-sauce <type>", "Add bbq sauce")
      .option("-c, --cheese [type]", "Add cheese", default="pepper jack")
      .option("-l, --olives [type]", "Add olives")
      .option("-L, --lettuce [type]", "Add lettuce", default="iceberg")
      .option("-n, --num [num]", "Number of pizzas", default=1, fn=(_.toInt))
  }

  def testParse() = {
    var fakeArgs = Array("-po", "unknown1", "--bbq-sauce=sweet", "--cheese", "cheddar", "-l", "black", "unknown2", "unknown3", "-n", "10")
    var program = testProgram.parse(fakeArgs)

    assertResult(program.peppers, "peppers was given in a combined short opt") { true }

    assertResult(program.bbqSauce, "bbq sauce was given as a long opt with equals sign") { "sweet" }

    assertResult(program.cheese, "cheese was given as a long opt") { "cheddar" }

    assertResult(program.onions, "onions was given in a combined short opt") { true }

    assertResult(program.anchovies, "anchovies has no param and was not present") { false }

    assertResult(program.olives, "olives was given a param as a short opt") { "black" }

    assertResult(program.lettuce, "lettuce was not given, but has a default") { "iceberg" }

    assertResult(program.num.getClass.getName, "num was coerced to an int") { "java.lang.Integer" }
    assertResult(program.num, "num should be parsed as an int") { 10 }

    assertResult(program.args, "args should contain the unknown args") {
      List("unknown1", "unknown2", "unknown3")
    }
  }

  def main(args: Array[String]) { 
    testParse
  }
}
