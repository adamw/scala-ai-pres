package pres

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

    val result: AgentResult[Either[AgentFailure, String]] =
      agent.run("Make coffee and play 500 miles")(backend)

    // case class AgentResult[T](
    //     finalAnswer: T,
    //     iterations: Int,
    //     toolCalls: Seq[ToolCallRecord],
    //     finishReason: FinishReason,
    //     usage: TokenUsage,
    //     llmCalls: Seq[LlmCallUsage],
    //     history: ConversationHistory)
    //
    // case class ToolCallRecord(id: String, toolName: String, input: String, output: String,
    //     iteration: Int)
    //
    // enum FinishReason:
    //   case NaturalStop, MaxIterations, TokenLimit, BudgetExceeded
    //   case Error(message: String)
    //   case Custom(reason: String)
    //
    // case class TokenUsage(inputTokens: Tokens, outputTokens: Tokens, cachedInputTokens: Tokens,
    //     reasoningTokens: Tokens, cacheWriteInputTokens: Tokens)
    //
    // case class LlmCallUsage(model: Option[String], usage: TokenUsage)
    //
    // case class ConversationHistory(entries: Seq[ConversationEntry])
    //
    // enum ConversationEntry:
    //   case UserPrompt(content: String)
    //   case AssistantResponse(content: String, toolCalls: Seq[ToolCall])
    //   case ToolResult(toolCallId: String, toolName: String, result: String)
    //   case IterationMarker(currentIteration: Int, maxIterations: Int)

    AgentResultPrinter(result)
  finally backend.close()
