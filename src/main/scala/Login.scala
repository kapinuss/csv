import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class Login (user: String, ip: String, dateTime: LocalDateTime)
object Login {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(nick: String, ip: String, dateTime: String): Login = new Login(nick, ip, performDate(dateTime))
  def performDate(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, formatter)
}
case class PluralLogin(ip: String, start: String, stop: String, users: String)//users = ip1:time1,ip2:time2
object PluralLogin {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(ip: String, start: String, stop: String, users: String): PluralLogin = new PluralLogin(ip, start, stop, users)
  def apply(logins: List[Login]): PluralLogin = new PluralLogin(logins.head.ip, logins.head.dateTime.toString, logins.last.dateTime.toString, formUsers(logins))
  def performDate(dateTime: LocalDateTime) : String = dateTime.format(formatter)
  def formUsers(similarLogins: List[Login]): String = {"mock"}
}
