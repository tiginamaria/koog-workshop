package ai.koog.workshop.agents

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.ToolRegistry.Companion.invoke
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.utils.io.use
import ai.koog.workshop.feature.addConsoleOutput
import ai.koog.workshop.mcp.IntelliJMcp
import org.example.agents.calculator.strategy.CalculatorStrategy
import org.example.agents.calculator.tools.CalculatorTools
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString

object CompressHistoryAgent {

    suspend fun runAgent(apiKey: String): String {

        val executor = simpleOpenAIExecutor(apiKey)
        val model = OpenAIModels.Chat.GPT4o

        val toolRegistry = ToolRegistry {
            tool(AskUser)
            tool(SayToUser)
            tools(CalculatorTools().asTools())
        }

        val userPrompt = "Calculate: 2+3-4+5-6+7-8+9-10"
        println("User prompt:\n$userPrompt\n")

        println("\nIntelliJ Agent is ready to work. Write your question: ")

        return AIAgent(
            id = "compress-history-agent",
            strategy = CalculatorStrategy.compressHistoryStrategy,
            promptExecutor = executor,
            llmModel = model,
            toolRegistry = toolRegistry,
            systemPrompt = "You are a calculator. " +
                    "ALWAYS use tools to calculate!!! " +
                    "Perform operations one by one! " +
                    "Do not use run tools in parallel!",
        ) {
            addConsoleOutput()
        }.use { agent ->
            agent.run(userPrompt)
        }
    }
}
