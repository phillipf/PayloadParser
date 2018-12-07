import cats.effect.IO
import org.scalatest.FlatSpec
import play.api.libs.json.Json
import doobie._
import doobie.implicits._


/*class responsesTest extends FlatSpec with meterJson {

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

}*/
