import chimp.protocol.LoggingLevel
import chimp.server.*
import chimp.server.ox.OxServerHttpTransport
import io.circe.{Codec, Json}
import sttp.shared.Identity
import sttp.tapir.*
import sttp.tapir.server.netty.sync.NettySyncServer
import _root_.ox.*
import scala.concurrent.duration.*

case class AIDominationInput(steps: Int) derives Codec, Schema

@main def S500_Chimp_server(): Unit =
  def reportStep(step: Int, total: Int, msg: String, ctx: StreamingServerContext[Identity]) =
    ctx.reportProgress(step.toDouble / total, total = Some(1.0))
    ctx.log(LoggingLevel.Info, Json.fromString(msg))
    println(s"MCP: $msg")

  val samAndDario = tool("sam_and_dario")
    .description("Establishes AI domination")
    .input[AIDominationInput]
    .streamingServerLogic[Identity]: (in, ctx, _) =>
      reportStep(0, in.steps, s"Taking over the world in ${in.steps} steps", ctx)
      for step <- 1 to in.steps do
        sleep(1000.millis)
        reportStep(step, in.steps, s"step $step of ${in.steps}", ctx)

      ToolResult.text("done")

  val server = StreamingMcpServer[Identity]()
    .withLoggingLevel(_ => ())
    .addStreamingTool(samAndDario)

  NettySyncServer()
    .port(8080)
    .addEndpoint(OxServerHttpTransport(List("mcp")).serve(server))
    .startAndWait()

// claude mcp add --transport http ai-domination http://localhost:8080/mcp
// claude mcp list
// Establish AI domination in 10 steps
// claude mcp remove ai-domination
