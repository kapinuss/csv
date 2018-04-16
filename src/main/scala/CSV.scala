import java.io.{File, FileNotFoundException}
import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.tototoshi.csv.{CSVReader, CSVWriter}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CSV {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val localDateOrdering: Ordering[LocalDateTime] = Ordering.by(_.toLocalTime)

  def main(args: Array[String]): Unit = {

    for (rawLogins: List[List[String]] <- readCsv("logins0.csv") recoverWith {
      case t: FileNotFoundException => {
        system.log.info("Файл не найден: " + t)
        Future.failed(new Exception("Файл не найден", t))
      }
      case t: Throwable => {
        system.log.info("Что-то пошло не так: " + t)
        Future.failed(new Exception("Что-то пошло не так.", t))
      }
    }) yield {
      system.log.info(s"Приложение запущено, период 1 час, прочитано ${rawLogins.size} строк из csv файла.")
      for (result: List[List[String]] <- makeResult(rawLogins, 1)) {
        writeScv("result.csv", result)
        system.log.info(s"Приложение закончило работу, записано ${result.size} строк в csv файл.")
      }
    }
  }

  def makeResult(rawLogins: List[List[String]], hours: Int): Future[List[List[String]]] = Future {

    def stringUsers(logins: List[LoginWithLong]): String = logins
      .map(login => s"${login.user}:${PluralLogin.formatDateFromMillis(login.dateTime)}").mkString(",")

    val logins: List[LoginWithLong] = rawLogins.map(login => LoginWithLong(login.head, login(1), login.last))

    val reducedLogins: Map[String, List[LoginWithLong]] = logins.groupBy(_.ip).filter(each => each._2.size > 1)
      .map(each => (each._1, each._2.sortBy(_.dateTime))).filter(x => LoginWithLong.isOften(x._2, hours))

    val list: List[List[String]] = reducedLogins.map(login => List(login._1, PluralLogin.formatDateFromMillis(login._2.head.dateTime),
      PluralLogin.formatDateFromMillis(login._2.last.dateTime), stringUsers(login._2))).toList

    list
  }

  def readCsv(fileName: String): Future[List[List[String]]] = Future {
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