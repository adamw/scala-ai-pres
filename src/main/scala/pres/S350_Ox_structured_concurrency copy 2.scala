package pres

import ox.*
import scala.concurrent.duration.*

object S355_Ox_concurrency:
  supervised {
    forkUser:
      sleep(1.second)
      println("Hello!")

    forkUser:
      sleep(500.millis)
      throw new RuntimeException("boom!")
  }
end S355_Ox_concurrency
