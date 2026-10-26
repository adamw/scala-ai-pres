package pres

import ox.*
import scala.concurrent.duration.*

object S350_Ox_structured_concurrency:
  supervised {
    forkUser:
      sleep(1.second)
      println("Hello!")

    forkUser:
      sleep(500.millis)
      throw new RuntimeException("boom!")
  }
