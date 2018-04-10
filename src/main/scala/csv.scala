import java.io.File
import java.net.URL

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.github.tototoshi.csv.{CSVReader, CSVWriter}

object csv {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {

    val x: List[List[String]] = readCsv("logins0.csv")
    println(x.size)
    writeScv("result.csv", x)
    val y: List[List[String]] = readCsv("result.csv")
    println(y.size)

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