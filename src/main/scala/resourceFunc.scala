import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

trait resourceFunc {

  //def resourcePath: String
  //def value: String

  val objectPattern = new Regex("""\[\d+, \d+, (.*)\]""")
  val intervalPattern = new Regex("""([,\\s]*\[\d+, \d+, \[[\d+|\d+, ]*\]\])""")
  val datapattern = new Regex("""\[(\d+), (\d+), \[([\d+|\d+, ]*)\]\]""", "timestamp", "interval", "data")

  def objects(x:String) = objectPattern.findAllMatchIn(x).map {
    case objectPattern(x) =>  x
  }.toList

  def intervalStrings(x:String) = intervalPattern.findAllIn(x).map {
    case intervalPattern(x) =>  x
  }.toList

  def intervals(x:String) = datapattern.findAllIn(x).map {
    case datapattern(x, y, z) => (x, y, z)
  }.toList

  def createIntervals(in: (String,String,List[String])): List[(Int, Int)] = {

    val payloads = in._3.map(_.toInt)
    //val initial = in._1.toInt + in._2.toInt

    val initial = in._1.toInt //Timestamp of first Interval [32-bit integer] representing the number of seconds since Jan 1st, 1970 in the UTC time zone.
    val step = in._2.toInt //Interval Period in seconds [32-bit integer]

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

  def simplifyjsonStep1(in: String) = {

    val intervalData = objects(in).flatMap(intervalStrings).flatMap(intervals)
    intervalData

  }

  def simplifyjsonStep2(in: List[(String,String,String)]) = {
    val split = in.map {
      case(x,y,z) => (x, y, z.split(", ").toList)
    }
    split.flatMap(createIntervals)

  }



}
