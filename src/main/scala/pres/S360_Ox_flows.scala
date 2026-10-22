package pres

import ox.*
import scala.concurrent.duration.*
import ox.flow.Flow

object S360_Ox_flows:
  case class Reading(sensor: String, tempC: Int)

  val cycle: Vector[Int] = Vector(17, 19, 21, 23, 20)

  def sensor(name: String, every: FiniteDuration): Flow[Reading] =
    Flow
      .tick(every)
      .zipWithIndex
      .map((_, i) => Reading(name, cycle((i % cycle.size).toInt)))

  def warmReadings(): List[Reading] =
    sensor("north", 10.millis)
      .merge(sensor("south", 25.millis))
      .filter(_.tempC >= 20)
      .take(6)
      .runToList()
