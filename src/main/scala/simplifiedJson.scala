

final case class simplifiedJson(serialNumber: String, metric: String, value: Option[Int], timestamp: BigInt) {


  def toTsdb = tsdbPayload(this.metric, this.value, this.timestamp, tsdbTags(this.serialNumber))

}
