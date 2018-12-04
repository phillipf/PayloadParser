import org.scalatest.FlatSpec

import java.io.File

import play.api.libs.json._

import scala.io.Source

class performance extends FlatSpec with meterJson {

  val filename = new File("./src/test/data/jsonMulti1.json")
  val lines = Source.fromFile(filename).getLines.toList.mkString
  val json = Json.parse(lines).as[List[Meter]]

  def time[R](block: => R): Double = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    //println("Elapsed time: " + (t1 - t0)/1000000000.00 + "seconds")
    (t1 - t0)/1000000000.00
  }

  val performanceSinglePayload = time { json.map(_.simplified) }

  "A single payload" should "be less than 5 seconds" in {
    assert(performanceSinglePayload < 5)
  }

  /*def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
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

  val jsonMulti1 = getMultiPayloads("./src/test/data/Nb-IoT Payloads_imei-863703032742533_19-10-2018")
  val jsonMulti2 = getMultiPayloads("./src/test/data/NB-IoT Payloads_19-10-2018")*/

  val filename2 = new File("./src/test/data/src/test/data/jsonMulti2.json")
  val lines2 = Source.fromFile(filename).getLines.toList.mkString
  val json2 = Json.parse(lines).as[List[Meter]]

  val performanceMultiPayload1 = time { json2.map(_.simplified) }
  //val performanceMultiPayload2 = time { jsonMulti2.map(_.simplified) }

  "multiple payloads" should "be less than 5 seconds" in {
    assert(performanceMultiPayload1 < 5)
    //assert(performanceMultiPayload2 < 5)
  }

}
