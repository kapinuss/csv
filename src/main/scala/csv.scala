import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer

object csv {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {

    //val actorAlpakka: ActorRef = system.actorOf(Props[CassandraAlpakka], "actorAlpakka")
    //val actorPhantom: ActorRef = system.actorOf(Props[CassandraPhantom], "actorPhantom")

    //actorAlpakka ! Update("3")
    //actorPhantom ! "Act!"
  }
}