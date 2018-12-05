
final case class Responses(serialNumber: String, address: Option[String], freeFormAddress: Option[String],
                           resources: List[Resources], imsi: Option[String], creationDate: Option[String],
                           result: Result, groupName: Option[String], protocol: String, requestId: String,
                           model: Option[String], firmwareVersion: Option[String], make: Option[String], timestamp: BigInt) {

  val jsonPayloads: simplifiedJson = simplifiedJson("responses", None, this.timestamp, this.serialNumber, "")
  /*def createIntervals(in: (String,String,List[String])): List[(Int, Int)] = {

    val payloads = in._3.map(_.toInt)
    //val initial = in._1.toInt + in._2.toInt
    val initial = in._1.toInt
    val step = in._2.toInt

    val result = new ListBuffer[(Int, Int)]()
    /*result += ((initial + step, payloads.head))*/
    def loop(in: List[Int] = payloads,
             initial: Int = initial,
             step: Int = step,
             res: ListBuffer[(Int,Int)] = result): List[(Int, Int)] = in match {
      case Nil => res.toList
      case x::xtail => loop(xtail, initial + step, step, res += ((initial, x)))
    }

    loop()
  }

  val simplifyjsonStep1 = {

    val in = this.value
    val objectPattern = new Regex("""\[\d+, \d+, (.*)\]""")
    val intervalPattern = new Regex("""([,\\s]*\[\d+, \d+, \[[\d+|\d+, ]*\]\])""")
    val datapattern = new Regex("""\[(\d+), (\d+), \[([\d+|\d+, ]*)\]\]""", "timestamp", "interval", "data")

    def objects(x:String) = objectPattern.findAllMatchIn(x).map {
      case objectPattern(x) =>  x
    }.toList

    def intervalStrings(x:String) = intervalPattern.findAllIn(x).map {
      case intervalPattern(x) =>  x
    }.toList

    /*def intervals(x:String) = datapattern.findAllIn(x).map {
      case datapattern(x, y, z) => (x, y, z.split(", ").toList)
     }.toList*/
    def intervals(x:String) = datapattern.findAllIn(x).map {
      case datapattern(x, y, z) => (x, y, z)
    }.toList

    //val intervalData = objects(in).flatMap(intervalStrings).flatMap(intervals).flatMap(createIntervals)
    val intervalData = objects(in).flatMap(intervalStrings).flatMap(intervals)
    intervalData

  }

  val simplifyjsonStep2 = {
    val in = this.simplifyjsonStep1
    val split = in.map {
      case(x,y,z) => (x, y, z.split(", ").toList)
    }
    split.flatMap(createIntervals)

  }

  def finalPayload(s: (Int,Int)) = simplifiedJson(resourcePath, Some(s._2), s._1.toLong * 1000, serialNumber, subscriptionId)
  def intermediatePayload(s: (String,String,String)) = intermediateJson(resourcePath, s._2, Some(s._3), s._1.toLong * 1000, serialNumber, subscriptionId)

  val jsonInstermediatePayloads = simplifyjsonStep1.map(s => intermediatePayload(s))
  val jsonPayloads = simplifyjsonStep2.map(s => finalPayload(s))*/

}