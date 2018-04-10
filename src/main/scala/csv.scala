import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.github.tototoshi.csv.{CSVReader, CSVWriter}

case class Login (user: String, ip: String, dateTime: LocalDateTime)
object Login {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(nick: String, ip: String, dateTime: String): Login = new Login(nick, ip, performDate(dateTime))
  def performDate(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, formatter)
}
case class PluralLogin(ip: String, start: String, stop: String, users: String)//users = ip1:time1,ip2:time2
object PluralLogin {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(nick: String, ip: String, dateTime: String): Login = new Login(nick, ip, performDate(dateTime))
  def performDate(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, formatter)
}

object csv {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {

    val rawLogins: List[List[String]] = readCsv("logins0.csv")
    val logins: List[Login] = rawLogins.map(login => Login(login.head, login(1), login.last))

  }

  def readCsv(fileName: String): List[List[String]] = {
    val reader = CSVReader.open(new File(fileName))
    val result = reader.all
    reader.close
    result
  }

  def writeScv(fileName: String, resultList: List[List[String]]): Unit = {
    val writer = CSVWriter.open(new File(fileName))
    writer.writeAll(resultList)
    writer.close()
  }
}