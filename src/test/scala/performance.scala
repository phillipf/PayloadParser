import org.scalatest.FlatSpec
import java.io.File
import java.time.Instant

import play.api.libs.json._

import scala.io.Source

class performance extends FlatSpec {

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
  implicit val simplifiedFormat = Json.format[simplifiedJson]
  implicit val meterFormat = Json.format[Meter]

  val filename = new File("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018/cww-2018-10-18-16-08-54.json")
  val lines = Source.fromFile(filename).getLines.toList.mkString
  val json = Json.parse(lines).as[Meter]

  def time[R](block: => R): Double = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    //println("Elapsed time: " + (t1 - t0)/1000000000.00 + "seconds")
    (t1 - t0)/1000000000.00
  }

  val performanceSinglePayload = time { json.simplified }

  "A single payload" should "be less than 5 seconds" in {
    assert(performanceSinglePayload < 5)
  }


  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def getMultiPayloads(path: String): List[Meter] = {
    val okFileExtensions = List("json")

    val files = getListOfFiles(new File(path), okFileExtensions)

    val text = files.flatMap(f => Source.fromFile(f.getPath()).getLines().toList).mkString(", ")

    val text2 = ("[" +: text :+ "]").mkString

    Json.parse(text2).as[List[Meter]]
  }

  val jsonMulti1 = getMultiPayloads("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018")
  val jsonMulti2 = getMultiPayloads("C:/Users/farrelp1/Documents/DigitalMetering/data/NB-IoT Payloads_imei-863703032743002_23-10-2018")


  val performanceMultiPayload1 = time { jsonMulti1.map(_.simplified) }
  val performanceMultiPayload2 = time { jsonMulti2.map(_.simplified) }

  "multiple payloads" should "be less than 5 seconds" in {
    assert(performanceMultiPayload1 < 5)
    assert(performanceMultiPayload2 < 5)
  }

}
