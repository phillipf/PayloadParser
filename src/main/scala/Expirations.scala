
final case class Expirations(deviceType: Option[String], serialNumber: String, subscriptionId: String, timestamp: BigInt) {

  val jsonPayloads: simplifiedJson = simplifiedJson("expiration", None, this.timestamp, this.serialNumber, this.subscriptionId)

}
