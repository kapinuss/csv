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
    makeResult(rawLogins)

  }

  def makeResult(rawLogins: List[List[String]]): Map[String, List[LoginWithLong]] = {
    println("Size of rawLogins " + rawLogins.size)
    val logins: List[LoginWithLong] = rawLogins.map(login => LoginWithLong(login.head, login(1), login.last))
    val reducedLogins: Map[String, List[LoginWithLong]] = logins.groupBy(_.ip).filter(each => each._2.size > 1)
      .map(each => (each._1, each._2.sortBy(_.dateTime)))
    println("Size of reducedLogins " + reducedLogins.size)
    //reducedLogins.take(20).foreach(x => { print(x + " | ") ; val bool = LoginWithLong.isOften(x._2, 1) ; println(bool) })
    val result: Map[String, List[LoginWithLong]] = reducedLogins.filter(x => LoginWithLong.isOften(x._2, 1))
    println("Size of result " + result.size)
    val list: List[PluralLogin] = result.map(x => PluralLogin(x._1, PluralLogin.formatDateFromMillis(x._2.head.dateTime),
      PluralLogin.formatDateFromMillis(x._2.last.dateTime), "users")).toList
    list.take(20).foreach(println)
    result
  }

  def stringUsers(ls: List[LoginWithLong]): String = {
    "mock"
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