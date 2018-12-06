
final case class Meter(reports: List[Reports], registrations: List[String], deregistrations: List[String],
                       updates: List[Updates], expirations: List[Expirations], responses: List[Responses]) {

  val simplifiedReports: PartialFunction[Meter, List[simplifiedJson]] = {
    case x if x.reports.nonEmpty => this.reports.flatMap(s => s.jsonPayloads)
  }

  val simplifiedUpdates: PartialFunction[Meter, List[simplifiedJson]] = {
    case x if x.updates.nonEmpty => this.updates.map(s => s.jsonPayloads)
  }

  val simplifiedExpirations: PartialFunction[Meter, List[simplifiedJson]] = {
    case x if x.expirations.nonEmpty => this.expirations.map(s => s.jsonPayloads)
  }

  val simplifiedResponses: PartialFunction[Meter, List[simplifiedJson]] = {
    case x if x.responses.nonEmpty => this.responses.flatMap(s => s.jsonPayloads)
  }

  val simplified = simplifiedReports orElse simplifiedUpdates orElse simplifiedExpirations orElse simplifiedResponses

  val simplifiedJson = simplified(this)

  val intermediateJson = this.reports.flatMap(s => s.jsonInstermediatePayloads)

}

