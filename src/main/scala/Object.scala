import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object Object extends resourceFunc {
  //val resourcePath = "10262"
  val tester = """{"10262/0/2/5":"[10270, 0, [1543845600, 86400, [[36, 99]]]]","10262/0/2/4":"[10269, 0, [1543773600, 14400, [70, 69, 63, 64, 63, 60]]]","10262/0/2/6":"[10271, 0, [1543845600, 86400, [[68, 0]]]]","10262/0/2/1":"[10266, 1, [1543761000, 1800, [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 32, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 0, 0, 1, 56, 0, 0, 0, 1, 0, 11, 125, 0, 6, 2, 0, 0, 4, 0, 8, 0, 0]]]","10262/0/2/0":"[10266, 0, [1543845600, 86400, [44]]]","10262/0/2/3":"[10268, 0, [1543773600, 14400, [16, 15, 18, 21, 22, 18]]]","10262/0/2/2":"[10267, 0, [1543845600, 86400, [[1543807005, 19]]]]"}"""
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  val myMap = mapper.readValue[Map[String,String]](tester)

  def finalPayload(s: (Int,Int)) = simplifiedJson("", Some(s._2), s._1.toLong * 1000)
  //final case class Object(id: String)

  //"{"10262/0/2/5":"[10270, 0, [1543845600, 86400, [[36, 99]]]]","10262/0/2/4":"[10269, 0, [1543773600, 14400, [70, 69, 63, 64, 63, 60]]]","10262/0/2/6":"[10271, 0, [1543845600, 86400, [[68, 0]]]]","10262/0/2/1":"[10266, 1, [1543761000, 1800, [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 32, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 0, 0, 1, 56, 0, 0, 0, 1, 0, 11, 125, 0, 6, 2, 0, 0, 4, 0, 8, 0, 0]]]","10262/0/2/0":"[10266, 0, [1543845600, 86400, [44]]]","10262/0/2/3":"[10268, 0, [1543773600, 14400, [16, 15, 18, 21, 22, 18]]]","10262/0/2/2":"[10267, 0, [1543845600, 86400, [[1543807005, 19]]]]"}"

  /*def main(args: Array[String]): Unit = {



    //val test = objects(value)
    val test = myMap.mapValues(x => simplifyjsonStep2(simplifyjsonStep1(x))).flatMap {x =>
        x._2.map {y =>
          finalPayload(y).copy(metric = x._1)
        }
    }
    println(test)
    //println(cborData)
    //println(Json.toJson(processed))

  }*/
}
