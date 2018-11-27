import java.io._
import java.time.Instant

import scala.io.Source
import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

final case class Updates(serialNumber: String, subscriptionId: String, timestamp: BigInt, deviceType: Option[String]) {

  val jsonPayloads: simplifiedJson = simplifiedJson("update", None, this.timestamp.toString, this.serialNumber, this.subscriptionId)
  //val jsonPayloads = simplifyjson.map(s => finalPayload(s))
}
final case class simplifiedJson(metric: String, value: Option[Int], timestamp: String, meterid: String, subscriptionId: String)
final case class Reports(serialNumber: String, timestamp: BigInt, subscriptionId: String, resourcePath: String, value: String) {

  def createIntervals(in: (String,String,List[String])): List[(Int, Int)] = {

    val payloads = in._3.map(_.toInt)
    val initial = in._1.toInt + in._2.toInt
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

  val simplifyjson = {

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

    def intervals(x:String) = datapattern.findAllIn(x).map {
      case datapattern(x, y, z) => (x, y, z.split(", ").toList)
    }.toList

    val intervalData = objects(in).flatMap(intervalStrings).flatMap(intervals).flatMap(createIntervals)

    intervalData

  }

  def finalPayload(s: (Int,Int)) = simplifiedJson(resourcePath, Some(s._2), s._1.toString, serialNumber, subscriptionId)

  val jsonPayloads = simplifyjson.map(s => finalPayload(s))

}
final case class Expirations(deviceType: Option[String], serialNumber: String, subscriptionId: String, timestamp: BigInt)
final case class Meter(reports: List[Reports], registrations: List[String], deregistrations: List[String], updates: List[Updates], expirations: List[Expirations], responses: List[String]) {

  val simplifiedReports: PartialFunction[Meter, List[simplifiedJson]] = {
    case x if x.reports.nonEmpty => this.reports.flatMap(s => s.jsonPayloads)
  }

  val simplifiedUpdates: PartialFunction[Meter, List[simplifiedJson]] = {
    case x if x.updates.nonEmpty => this.updates.map(s => s.jsonPayloads)
  }

  val simplified = simplifiedReports orElse simplifiedUpdates

  val simplifiedJson = simplified(this)

}

object main extends App {

  //val filename = new File(args(0))
  //val output = new File(args(1))
  //multiple payloads
  //val filename = new File("C:/Users/farrelp1/Documents/payloadParser/src/test/data/jsonMulti2.json")
  //multiple payloads
  //val filename = new File("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018/cww-2018-10-18-16-08-54.json")
  //updates payloads
  val filename = new File("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018/cww-2018-10-18-16-07-46.json")

  val file = Source.fromFile(filename).getLines.toList.mkString

  val lines = if(file.head != '[') ("[" +: file :+ "]").mkString else file
  //println(lines)
  implicit val BigIntWrite: Writes[BigInt] = new Writes[BigInt] {
    override def writes(bigInt: BigInt): JsValue = JsString(bigInt.toString())
  }

  implicit val BigIntRead: Reads[BigInt] = Reads {
    case JsString(value) => JsSuccess(scala.math.BigInt(value))
    case JsNumber(value) => JsSuccess(value.toBigInt())
    case unknown => JsError(s"Invalid BigInt")
  }

  implicit val updatesFormat = Json.format[Updates]
  implicit val reportsFormat = Json.format[Reports]
  implicit val expirationFormat = Json.format[Expirations]
  implicit val meterFormat = Json.format[Meter]
  implicit val simplifiedFormat = Json.format[simplifiedJson]

  //println(Json.parse(lines).as[JsArray])

  val json = {
    Json.parse(lines).as[List[Meter]]
    //Json.parse(lines).as[Meter]
  }

  val processed = json.flatMap(_.simplifiedJson)
  //val processed = json.simplified

  def payloadwriter(file: String, output: List[simplifiedJson]) {
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))

    writer.write("METRIC| VALUE| TIMESTAMP| METERID| SUBSCRIPTIONID")

    writer.newLine()
    for (x <- output) {
      //println(BigInt(x.timestamp).toLong)
      val time = Instant.ofEpochSecond(BigInt(x.timestamp).toLong)
      writer.write(
        x.metric + "|" +
          x.value.getOrElse("NA") + "|" +
          time + "|" +
          x.meterid + "|" +
          x.subscriptionId + "\n")
    }// however you want to format it

    writer.close()
  }

  payloadwriter("output/payloads.csv", processed)
  /*val pw = new PrintWriter(output)
  pw.write(Json.toJson(processed).toString())
  pw.close*/

  println(Json.toJson(processed))
}
