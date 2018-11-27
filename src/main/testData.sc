import java.io.File


import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.matching.Regex

trait Id {

  def serialNumber: String
  def subscriptionId: String
  def timestamp: BigInt

}

//final case class Id(serialNumber: String, subscriptionId: String, timestamp: BigInt)

final case class Packet(meters: Array[Meter])

case class simplifiedJson(metric: String, value: Option[Int], timestamp: String, meterid:String, subscriptionId: String)

case class Updates(serialNumber: String, subscriptionId: String, timestamp: BigInt, deviceType: Option[String]) extends Id {

  val jsonPayload = simplifiedJson("update", None, this.timestamp.toString, this.serialNumber, this.subscriptionId)

}
final case class Expirations(deviceType: Option[String], serialNumber: String, subscriptionId: String, timestamp: BigInt) extends Id

final case class Reports(serialNumber: String, timestamp: BigInt, subscriptionId: String, resourcePath: String, value: String) extends Id {

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

  //val simplified = simplifyjson

  def finalPayload(s: (Int,Int)) = simplifiedJson(resourcePath, Some(s._2), s._1.toString, serialNumber, subscriptionId)

  val jsonPayloads = simplifyjson.map(s => finalPayload(s))

}

final case class Meter(reports: List[Reports], registrations: List[String], deregistrations: List[String], updates: List[Updates], expirations: List[Expirations], responses: List[String]) {

  //val simplified: List[simplifiedJson] = this.reports.flatMap(s => s.jsonPayloads)

  val simplifiedReports: PartialFunction[List[Reports], List[simplifiedJson]] = {
    case a: List[Reports] => this.reports.flatMap(s => s.jsonPayloads)
  }

  val simplifiedUpdates: PartialFunction[List[Updates], List[simplifiedJson]] = {
    case a: List[Updates] => this.updates.flatMap(x => x.)
  }

}

implicit val updatesFormat = Json.format[Updates]
implicit val reportsFormat = Json.format[Reports]
implicit val expirationFormat = Json.format[Expirations]
implicit val meterFormat = Json.format[Meter]
//implicit val packetFormat = Json.format[Packet]


implicit val BigIntWrite: Writes[BigInt] = new Writes[BigInt] {
  override def writes(bigInt: BigInt): JsValue = JsString(bigInt.toString())
}

implicit val BigIntRead: Reads[BigInt] = Reads {
  case JsString(value) => JsSuccess(scala.math.BigInt(value))
  case JsNumber(value) => JsSuccess(value.toBigInt())
  case unknown => JsError(s"Invalid BigInt")
}

def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
  dir.listFiles.filter(_.isFile).toList.filter { file =>
    extensions.exists(file.getName.endsWith(_))
  }
}

val okFileExtensions = List("json")

val files = getListOfFiles(new File("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018"), okFileExtensions)

val text = files.flatMap(f => Source.fromFile(f.getPath()).getLines().toList).mkString(", ")

val text2 = ("[" +: text :+ "]").mkString

val json = Json.parse(text2).as[List[Meter]]

def time[R](block: => R): Double = {
  val t0 = System.nanoTime()
  val result = block    // call-by-name
  val t1 = System.nanoTime()
  //println("Elapsed time: " + (t1 - t0)/1000000000.00 + "seconds")
  (t1 - t0)/1000000000.00
}

json.map(_.simplified)

val performance = time { json.map(_.simplified) }

def getMultiPayloads(path: String): List[Meter] = {

  val okFileExtensions = List("json")

  val files = getListOfFiles(new File(path), okFileExtensions)

  val text = files.flatMap(f => Source.fromFile(f.getPath()).getLines().toList).mkString(", ")

  val text2 = ("[" +: text :+ "]").mkString

  Json.parse(text2).as[List[Meter]]
}

val jsonMulti1 = getMultiPayloads("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018")

val jsonMulti2 = getMultiPayloads("C:/Users/farrelp1/Documents/DigitalMetering/data/NB-IoT Payloads_imei-863703032743002_23-10-2018")

Json.toJson(jsonMulti2)

/*import java.io._
val pw = new PrintWriter(new File("C:/Users/farrelp1/Documents/payloadParser/src/test/data/jsonMulti2.json" ))
pw.write(Json.toJson(jsonMulti2).toString())
pw.close*/

//val performanceMultiPayload1 = time { jsonMulti1.map(_.simplified) }
//val performanceMultiPayload2 = time { jsonMulti2.map(_.simplified) }