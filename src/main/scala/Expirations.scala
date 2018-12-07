
final case class Expirations(deviceType: Option[String], serialNumber: String, subscriptionId: String, timestamp: BigInt) {

  val jsonPayloads: simplifiedJson = simplifiedJson(this.serialNumber, "expiration", None, this.timestamp)

}
