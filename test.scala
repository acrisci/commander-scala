object Test {
  def main(args: Array[String]) { 
    var fakeArgs = Array("-po", "unknown1", "--bbq-sauce=sweet", "--cheese", "cheddar", "-l", "black", "unknown2", "unknown3")
    var program = new Program()
    program
      .version("1.0.0")
      .option("-p, --peppers", "Add peppers")
      .option("-o, --onions", "Add onions")
      .option("-a, --anchovies", "Add anchovies")
      .option("-b, --bbq-sauce <type>", "Add bbq sauce")
      .option("-c, --cheese [type]", "Add cheese", default="pepper jack")
      .option("-l, --olives [type]", "Add olives")
      .parse(fakeArgs)

    var peppers = program.peppers
    println("peppers: %s", peppers)
    var bbqSauce = program.bbqSauce
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
    var unknownArgs = program.unknownArgs
    println("unknown args: " + unknownArgs)
  }
}
