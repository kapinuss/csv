import java.io.File
import java.time.LocalDateTime
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.github.tototoshi.csv.{CSVReader, CSVWriter}

object CSV {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val localDateOrdering: Ordering[LocalDateTime] = Ordering.by(_.toLocalTime)

  def main(args: Array[String]): Unit = {

    val rawLogins: List[List[String]] = readCsv("logins0.csv")
    val result: List[List[String]] = makeResult(rawLogins)
    writeScv("result.csv", result)

  }

  def makeResult(rawLogins: List[List[String]]): List[List[String]] = {
    val logins: List[LoginWithLong] = rawLogins.map(login => LoginWithLong(login.head, login(1), login.last))
    val reducedLogins: Map[String, List[LoginWithLong]] = logins.groupBy(_.ip).filter(each => each._2.size > 1)
      .map(each => (each._1, each._2.sortBy(_.dateTime))).filter(x => LoginWithLong.isOften(x._2, 1))
    val list: List[List[String]] = reducedLogins.map(login => List(login._1, PluralLogin.formatDateFromMillis(login._2.head.dateTime),
      PluralLogin.formatDateFromMillis(login._2.last.dateTime), stringUsers(login._2))).toList
    list
  }

  def stringUsers(logins: List[LoginWithLong]): String = logins.map(login => s"${login.user}:${PluralLogin.formatDateFromMillis(login.dateTime)}").mkString(",")

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