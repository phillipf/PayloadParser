import java.io._
import java.text.SimpleDateFormat

import scala.io.Source
import play.api.libs.json._


object main extends App with meterJson {

  //val filename = new File(args(0))
  //val output = new File(args(1))
  //multiple payloads
  val filename = new File("./src/test/data/jsonMulti2.json")
  //multiple payloads
  //val filename = new File("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018/cww-2018-10-18-16-08-54.json")
  //updates payloads
  //val filename = new File("C:/Users/farrelp1/Documents/DigitalMetering/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018/cww-2018-10-18-16-07-46.json")

  val file = Source.fromFile(filename).getLines.toList.mkString

  val lines = if(file.head != '[') ("[" +: file :+ "]").mkString else file
  //println(lines)
   //println(Json.parse(lines).as[JsArray])

  val json = {
    Json.parse(lines).as[List[Meter]]
    //Json.parse(lines).as[Meter]
  }

  val intermediate = json.flatMap(_.intermediateJson)
  val processed = json.flatMap(_.simplifiedJson)
  //val processed = json.simplified

  def epochToDate(epochMillis: Long): String = {
    val df:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    df.format(epochMillis)
  }

  println(epochToDate(processed.head.timestamp.toLong))

  payloadwriterFinal("output/payloads.csv", processed)
  payloadwriterIntermediate("output/payloadsIntermediate.csv", intermediate)
  /*val pw = new PrintWriter(output)
  pw.write(Json.toJson(processed).toString())
  pw.close*/

  println(Json.toJson(processed))
}
