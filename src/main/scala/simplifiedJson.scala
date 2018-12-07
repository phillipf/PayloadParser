

final case class simplifiedJson(serialNumber: String, metric: String, value: Option[Int], timestamp: BigInt) {


  def toTsdb = tsdbPayload(this.metric, this.timestamp, this.value, tsdbTags(this.serialNumber))

}
