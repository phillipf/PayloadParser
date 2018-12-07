import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}

import main.epochToDate
import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

trait meterJson {

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
  implicit val resourcesFormat = Json.format[Resources]
  implicit val resultFormat = Json.format[Result]
  implicit val responsesFormat = Json.format[Responses]
  implicit val meterFormat = Json.format[Meter]
  implicit val tsdbTagsFormat = Json.format[tsdbTags]
  implicit val tsdbPayloadFormat = Json.format[tsdbPayload]
  implicit val simplifiedFormat = Json.format[simplifiedJson]

  def payloadwriterFinal(file: String, output: List[simplifiedJson]) {
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))

    writer.write("METRIC| VALUE| TIMESTAMP| METERID| SUBSCRIPTIONID")

    writer.newLine()
    for (x <- output) {
      //println(x.timestamp.toString.toLong)
      //val time = Instant.ofEpochSecond(x.timestamp.toString.toLong)
      val time = epochToDate(x.timestamp.toLong)
      writer.write(
        x.metric + "|" +
          x.value.getOrElse("NA") + "|" +
          time + "|" +
          //x.meterid + "|" +
          "\n")
    }// however you want to format it

    writer.close()
  }

  def payloadwriterIntermediate(file: String, output: List[intermediateJson]) {
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))

    writer.write("METRIC| OBJECTREF| VALUE| TIMESTAMP| METERID| SUBSCRIPTIONID")

    writer.newLine()
    for (x <- output) {
      //println(x.timestamp.toString.toLong)
      //val time = Instant.ofEpochSecond(x.timestamp.toString.toLong)
      val time = epochToDate(x.timestamp.toLong)
      writer.write(
        x.metric + "|" +
          x.value.getOrElse("NA") + "|" +
          x.objectReference + "|" +
          time + "|" +
          //x.meterid + "|" +
          "\n")
    }// however you want to format it

    writer.close()
  }



}
