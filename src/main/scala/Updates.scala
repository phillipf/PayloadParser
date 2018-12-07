
final case class Updates(serialNumber: String, subscriptionId: String, timestamp: BigInt, deviceType: Option[String]) {

  val jsonPayloads: simplifiedJson = simplifiedJson(this.serialNumber, "update", None, this.timestamp
    //,  this.subscriptionId
  )

}
