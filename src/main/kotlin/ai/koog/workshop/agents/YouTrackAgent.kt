package ai.koog.workshop.agents

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.utils.io.use
import ai.koog.workshop.mcp.YouTrackMcpMcp

object YouTrackAgent {

    suspend fun runAgent(apiKey: String) {

        val executor = simpleOpenAIExecutor(apiKey)
        val model = OpenAIModels.Chat.GPT4o

        val youtrackClient = YouTrackMcpMcp()
        val process = youtrackClient.runProcess()

        try {

            // Create the ToolRegistry with tools from the YouTrack MCP server
            val toolRegistry = youtrackClient.createToolRegistry(process)

            toolRegistry.tools.forEachIndexed { index, tool ->
                println("YouTrack available tools: ")
                println("$index: '${tool.name}' - ${tool.description}")
            }

            AIAgent(
                promptExecutor = executor,
                llmModel = model,
                toolRegistry = toolRegistry,
            ).use { agent ->

                println("YouTrack Agent is ready to work. Write your question: ")
                val userPrompt = "Show me the number of issues in KG project"
                println("User prompt: $userPrompt")

                agent.run(
                    "$userPrompt\n" +
                            "You can only call tools. Get it by calling YouTrack tools."
                )
            }
        }
        catch (e: Exception) {
            println("YouTrack MCP Exception: ${e.message}")
        }
        finally {
            println("YouTrack MCP server process is destroying.")
            process.destroy()
        }
    }
}
