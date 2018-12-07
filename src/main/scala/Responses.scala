
final case class Responses(serialNumber: String, address: Option[String], freeFormAddress: Option[String],
                           resources: List[Resources], imsi: Option[String], creationDate: Option[String],
                           result: Result, groupName: Option[String], protocol: String, requestId: String,
                           model: Option[String], firmwareVersion: Option[String], make: Option[String],
                           timestamp: BigInt) extends meterJson with resourceFunc {


  val objectMap: List[Map[String, String]] = this.resources.map(_.myMap)

  def finalPayload(s: (Int,Int)) = simplifiedJson(serialNumber, "", Some(s._2), s._1.toLong * 1000)
  def intermediatePayload(s: (String,String,String)) = intermediateJson("", s._2, Some(s._3), s._1.toLong * 1000)

  val jsonInstermediatePayloads: List[intermediateJson] = objectMap.flatMap(_.mapValues(x => simplifyjsonStep1(x)).flatMap { x =>
    x._2.map { y =>
      intermediatePayload(y).copy(metric = x._1)
    }
  })

  val jsonPayloads: List[simplifiedJson] = objectMap.flatMap(_.mapValues(x =>
    simplifyjsonStep2(simplifyjsonStep1(x))).flatMap {x =>
    x._2.map {y =>
      finalPayload(y).copy(metric = x._1)
    }
  })
  //val jsonPayloads: simplifiedJson = simplifiedJson("responses", None, this.timestamp
    //, this.serialNumber, ""



}