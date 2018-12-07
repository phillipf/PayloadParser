
final case class tsdbPayload(metric: String, value: Option[Int], timestamp: BigInt, tags: tsdbTags) {

}
