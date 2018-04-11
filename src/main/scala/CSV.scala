import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.github.tototoshi.csv.{CSVReader, CSVWriter}


object CSV {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {

    implicit val localDateOrdering: Ordering[LocalDateTime] = Ordering.by(_.toLocalTime)

    val rawLogins: List[List[String]] = readCsv("logins0.csv")
    //val logins: List[Login] = rawLogins.map(login => Login(login.head, login(1), login.last))

    val logins: List[LoginWithDate] = rawLogins.map(login => LoginWithDate(login.head, login(1), login.last))
    val reducedLogins: Map[String, List[LoginWithDate]] = logins.groupBy(_.ip).filter(each => each._2.size > 1)
      .map(each => (each._1, each._2.sortBy(_.dateTime)))
    println("Size of reducedLogins " + reducedLogins.size)
    reducedLogins.take(20).foreach(x => { print(x + " | ") ; val pairs = LoginWithDate.isOften(x._2) })
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