import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

final case class Resources(resourcePath: String, value: String) extends resourceFunc {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  val myMap = mapper.readValue[Map[String,String]](value)

  //myMap.mapValues(simplifyjsonStep1)




}
