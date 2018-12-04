
final case class Updates(serialNumber: String, subscriptionId: String, timestamp: BigInt, deviceType: Option[String]) {

  val jsonPayloads: simplifiedJson = simplifiedJson("update", None, this.timestamp, this.serialNumber, this.subscriptionId)

}
