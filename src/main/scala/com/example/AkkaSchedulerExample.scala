package com.example

import java.time.ZonedDateTime

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.example.Printer.Greeting
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object Printer {
  def props: Props = Props[Printer]
  final case class Greeting(greeting: String)
}

class Printer extends Actor with ActorLogging {
  import Printer._

  def receive: PartialFunction[Any, Unit] = {
    case Greeting(greeting) =>
      log.info(s"Greeting received: $greeting at ${ZonedDateTime.now()}")
  }
}

object AkkaSchedulerExample extends App {

  val system: ActorSystem = ActorSystem("akka-quartz-scheduler-example")

  val printer: ActorRef = system.actorOf(Printer.props, "printerActor")

  printer ! Greeting("test message")

  system.scheduler.schedule(initialDelay = 10.seconds, interval = 1.minute) {
    println("Executing something...")
  }

  QuartzSchedulerExtension(system).createSchedule("schedule_name", Some("description"), "0 0 12 ? * *")
  QuartzSchedulerExtension(system).schedule("schedule_name", printer, Greeting("Scheduled Message"))

}
