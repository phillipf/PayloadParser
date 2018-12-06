import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

final case class Resources(resourcePath: String, value: String) extends resourceFunc {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  val myMap = mapper.readValue[Map[String,String]](value)

  myMap.mapValues(simplifyjsonStep1)

  def finalPayload(s: (Int,Int)) = simplifiedJson("", Some(s._2), s._1.toLong * 1000)
  def intermediatePayload(s: (String,String,String)) = intermediateJson("", s._2, Some(s._3), s._1.toLong * 1000)

  val jsonInstermediatePayloads = myMap.mapValues(x => simplifyjsonStep1(x)).flatMap { x =>
    x._2.map { y =>
      intermediatePayload(y).copy(metric = x._1)
    }
  }

  val jsonPayloads = myMap.mapValues(x =>
    simplifyjsonStep2(simplifyjsonStep1(x))).flatMap {x =>
      x._2.map {y =>
        finalPayload(y).copy(metric = x._1)
      }
  }


}
