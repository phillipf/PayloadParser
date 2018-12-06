import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

final case class Reports(serialNumber: String, timestamp: BigInt, subscriptionId: String, resourcePath: String, value: String) extends meterJson with resourceFunc {

  def finalPayload(s: (Int,Int)) = simplifiedJson(resourcePath, Some(s._2), s._1.toLong * 1000)
  def intermediatePayload(s: (String,String,String)) = intermediateJson(resourcePath, s._2, Some(s._3), s._1.toLong * 1000)

  val jsonInstermediatePayloads = simplifyjsonStep1(value).map(s => intermediatePayload(s))
  val jsonPayloads = simplifyjsonStep2(simplifyjsonStep1(value)).map(s => finalPayload(s))
}
