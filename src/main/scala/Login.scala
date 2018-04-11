import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

sealed trait Login
final case class LoginWithDate (user: String, ip: String, dateTime: LocalDateTime) extends Login
object Login {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(nick: String, ip: String, dateTime: String): LoginWithDate = new LoginWithDate(nick, ip, performDate(dateTime))
  def performDate(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, formatter)
}
final case class LoginWithInt (user: String, ip: String, dateTime: Long) extends Login
object LoginWithInt {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(nick: String, ip: String, dateTime: String): LoginWithInt = new LoginWithInt(nick, ip, performDate(dateTime))
  def performDate(dateTime: String): Long = LocalDateTime.parse(dateTime, formatter).atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
}
case class PluralLogin(ip: String, start: String, stop: String, users: String)//users = ip1:time1,ip2:time2
object PluralLogin {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(ip: String, start: String, stop: String, users: String): PluralLogin = new PluralLogin(ip, start, stop, users)
  def apply(logins: List[LoginWithDate]): PluralLogin = new PluralLogin(logins.head.ip, logins.head.dateTime.toString, logins.last.dateTime.toString, formUsers(logins))
  def performDate(dateTime: LocalDateTime) : String = dateTime.format(formatter)
  def formUsers(similarLogins: List[Login]): String = {"mock"}
}
