
final case class Responses(serialNumber: String, address: Option[String], freeFormAddress: Option[String],
                           resources: List[Resources], imsi: Option[String], creationDate: Option[String],
                           result: Result, groupName: Option[String], protocol: String, requestId: String,
                           model: Option[String], firmwareVersion: Option[String], make: Option[String],
                           timestamp: BigInt) extends meterJson {

  val jsonPayloads: List[simplifiedJson] = this.resources.flatMap(_.jsonPayloads)

  //val jsonPayloads: simplifiedJson = simplifiedJson("responses", None, this.timestamp
    //, this.serialNumber, ""



}