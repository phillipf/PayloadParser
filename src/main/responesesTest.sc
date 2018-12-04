import cats.effect.IO
import doobie.Transactor
import play.api.libs.json._
import doobie.implicits._


implicit val BigIntWrite: Writes[BigInt] = new Writes[BigInt] {
  override def writes(bigInt: BigInt): JsValue = JsString(bigInt.toString())
}

implicit val BigIntRead: Reads[BigInt] = Reads {
  case JsString(value) => JsSuccess(scala.math.BigInt(value))
  case JsNumber(value) => JsSuccess(value.toBigInt())
  case unknown => JsError(s"Invalid BigInt")
}

implicit val updatesFormat = Json.format[Updates]
implicit val reportsFormat = Json.format[Reports]
implicit val expirationFormat = Json.format[Expirations]
implicit val resourcesFormat = Json.format[Resources]
implicit val responsesFormat = Json.format[Responses]
implicit val meterFormat = Json.format[Meter]
implicit val simplifiedFormat = Json.format[simplifiedJson]

val xa = Transactor.fromDriverManager[IO](
  "com.microsoft.sqlserver.jdbc.SQLServerDriver",
  "jdbc:sqlserver://;servername=wvdpdevsql01;databaseName=ODS_CWWDM;integratedSecurity=true;"
)

val payloads: String =
  sql"""SELECT [PAYLOAD]
  FROM [ODS_CWWDM].[dbo].[NB_IOT_test]
  where INSTANCE_ID = '1f6202c7-8384-4b7f-85f1-93d1cf0c1e39-000249b8'
  order by [CURR_DATETIME] desc"""
    .query[String]
    .to[List]
    .transact(xa)
    .unsafeRunSync()
    .mkString

val lines = if(payloads.head != '[') ("[" +: payloads :+ "]").mkString else payloads
val json = Json.parse(lines).as[List[Meter]]