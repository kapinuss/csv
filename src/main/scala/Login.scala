import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

sealed trait Login

final case class LoginWithDate(user: String, ip: String, dateTime: LocalDateTime) extends Login

object LoginWithDate {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def apply(nick: String, ip: String, dateTime: String): LoginWithDate = new LoginWithDate(nick, ip, performDate(dateTime))

  def performDate(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, formatter)

  def isOften(logins: List[LoginWithDate], hours: Int): Boolean = {
    val timePoints: List[LocalDateTime] = logins.map(_.dateTime)
    val tuples: List[(LocalDateTime, LocalDateTime)] = timePoints.zip(timePoints.tail)
    tuples.exists(tuple => compareDates(tuple, hours))
  }

  def compareDates(dates: (LocalDateTime, LocalDateTime), hours: Int): Boolean = dates._1.plusHours(hours).isAfter(dates._2)
}

final case class LoginWithLong(user: String, ip: String, dateTime: Long) extends Login

object LoginWithLong {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def apply(nick: String, ip: String, dateTime: String): LoginWithLong = new LoginWithLong(nick, ip, performDate(dateTime))

  def performDate(dateTime: String): Long = LocalDateTime.parse(dateTime, formatter).atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
}

case class PluralLogin(ip: String, start: String, stop: String, users: String) //users = ip1:time1,ip2:time2

object PluralLogin {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def apply(ip: String, start: String, stop: String, users: String): PluralLogin = new PluralLogin(ip, start, stop, users)

  def apply(logins: List[LoginWithDate]): PluralLogin = new PluralLogin(logins.head.ip, logins.head.dateTime.toString, logins.last.dateTime.toString, formUsers(logins))

  def performDate(dateTime: LocalDateTime): String = dateTime.format(formatter)

  def formUsers(similarLogins: List[Login]): String = {
    "mock"
  }
}
