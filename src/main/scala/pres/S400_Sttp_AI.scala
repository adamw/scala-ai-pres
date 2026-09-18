import sttp.ai.core.agent.*
import sttp.ai.openai.OpenAI
import sttp.ai.openai.agent.OpenAIAgent
import sttp.ai.openai.requests.completions.chat.ChatRequestBody.ChatCompletionModel
import sttp.client4.DefaultSyncBackend
import sttp.tapir.Schema
import sttp.tapir.Schema.annotations.description

@main def S400_Sttp_AI() =
  case class BrewCoffeeInput(
      @description("espresso, latte or americano") kind: String,
      @description("number of cups") cups: Int
  ) derives io.circe.Codec.AsObject,
        Schema

  case class PlayMusicInput(
      @description("song title, optionally with the artist") song: String,
      @description("volume, 0-100") volume: Option[Int]
  ) derives io.circe.Codec.AsObject,
        Schema

  val brewCoffeeTool = AgentTool.fromFunction(
    "brew_coffee",
    "Brew coffee on the kitchen coffee machine"
  ): (input: BrewCoffeeInput) =>
    println(s"☕ brewing ${input.cups} x ${input.kind}")
    s"${input.cups} ${input.kind}(s) ready"

  val playMusicTool = AgentTool.fromFunction(
    "play_music",
    "Play a song on the living room speakers"
  ): (input: PlayMusicInput) =>
    val volume = input.volume.getOrElse(40)
    println(s"🎵 playing ${input.song} at volume $volume")
    s"Now playing: ${input.song} (volume $volume)"

  val backend = DefaultSyncBackend()
  try
    val agent = OpenAIAgent
      .synchronous(OpenAI.fromEnv, ChatCompletionModel.GPT4oMini)
      .maxIterations(5)
      .tools(brewCoffeeTool, playMusicTool)
      .build

    val result = agent.run("Make coffee and play 500 miles")(backend)

    AgentResultPrinter(result)
  finally backend.close()
