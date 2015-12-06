object Test {
  def main(args: Array[String]) { 
    var fakeArgs = Array("-po", "unknown1", "--bbq-sauce=sweet", "--cheese", "cheddar", "-l", "black", "unknown2", "unknown3", "-n", "10")
    var program = new Program()
      .version("1.0.0")
      .option("-p, --peppers", "Add peppers")
      .option("-o, --onions", "Add onions")
      .option("-a, --anchovies", "Add anchovies")
      .option("-b, --bbq-sauce <type>", "Add bbq sauce")
      .option("-c, --cheese [type]", "Add cheese", default="pepper jack")
      .option("-l, --olives [type]", "Add olives")
      .option("-n, --num [num]", "Number of pizzas", default=1, fn=(_.toInt))
      .parse(fakeArgs)

    var peppers = program.peppers
    println("peppers: " + peppers)
    var bbqSauce = program.bbqSauce
    if (bbqSauce == "sweet") {
      println("you ordered bbq sweet")
    }
    println("bbqSauce: " + bbqSauce)
    var cheese = program.cheese
    println("cheese: " + program.cheese)
    var onions = program.onions
    println("onions: " + program.onions)
    var anchovies = program.anchovies
    println("anchovies: " + program.anchovies)
    var olives = program.olives
    println("olives: " + program.olives)
    //println(peppers.asInstanceOf[List[Int]](0))
    var num = program.num
    println("num is a : " + num.getClass.getName)
    println("number of pizzas: " + num)
    var unknownArgs = program.args
    println("unknown args: " + unknownArgs)
  }
}
