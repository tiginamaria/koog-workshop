package ai.koog.workshop.agents

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.markdown.markdown
import ai.koog.utils.io.use
import ai.koog.workshop.mcp.IntelliJMcp
import java.nio.file.Path
import kotlin.io.path.absolutePathString

object IntelliJAgent {

    suspend fun runAgent(apiKey: String): String {

        // Create the ToolRegistry with tools from the IntelliJ MCP server
        val toolRegistry = IntelliJMcp().createToolRegistry()

        val executor = simpleOpenAIExecutor(apiKey)
        val model = OpenAIModels.Chat.GPT4o

        val userPrompt = createUserPrompt()

        return AIAgent(
            id = "intellij-mcp-agent",
            promptExecutor = executor,
            llmModel = model,
            toolRegistry = toolRegistry,
        ).use { agent ->

            agent.run(
                "$userPrompt\n" +
                        "You can only call tools. Get it by calling IntelliJ MCP tools."
            )
        }
    }

    internal fun createUserPrompt(): String {
        println("\nIntelliJ Agent is ready to work. Write your question: ")

        val userInput = readlnOrNull()
        val userPrompt =
            if (userInput.isNullOrBlank()) {
                markdown {
                    +"List all classes that mention 'intellij' in the current project: "
                    newline()
                    +Path.of(".").absolutePathString()
                    newline()

                    h3("Mention only class names. Each class on a singe line")
                    newline()
                    h3("Include index number for each class starting from 1")
                }
            } else {
                userInput
            }

        println("User prompt:\n$userPrompt\n")
        return userPrompt
    }
}
