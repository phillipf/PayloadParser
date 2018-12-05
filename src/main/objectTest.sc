import play.api.libs.json.Json

val tester = """{"10262/0/2/5":"[10270, 0, [1543845600, 86400, [[36, 99]]]]"}"""
Json.parse(tester)