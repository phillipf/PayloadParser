
final case class tsdbPayload(metric: String, timestamp: BigInt, value: Option[Int], tags: tsdbTags) {

}
