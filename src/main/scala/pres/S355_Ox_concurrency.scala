package pres

import ox.*
import scala.concurrent.duration.*
import ox.scheduling.*
import ox.resilience.retry

object S350_Ox_structured_concurrency:
  case class Quote(vendor: String, priceCents: Int)

  def ask(vendor: String, priceCents: Int, latency: FiniteDuration): Quote =
    sleep(latency)
    Quote(vendor, priceCents)

  def cheapest(): Quote =
    val (cached, acme, globex) = par(
      ask("cache", 1300, 10.millis),
      retry(Schedule.exponentialBackoff(50.millis).maxRetries(3))(
        ask("acme", 1250, 100.millis)
      ),
      timeoutOption(1.second)(ask("globex", 1100, 200.millis))
    )
    (List(cached, acme) ++ globex.toList).minBy(_.priceCents)
end S350_Ox_structured_concurrency
