package ai.koog.workshop.agents

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.utils.io.use
import ai.koog.workshop.agents.IntelliJAgent.createUserPrompt
import ai.koog.workshop.feature.addLangfuseExporter
import ai.koog.workshop.mcp.IntelliJMcp

object IntelliJOpenTelemetryAgent {

    suspend fun runAgent(apiKey: String): String {

        // Create the ToolRegistry with tools from the IntelliJ MCP server
        val toolRegistry = IntelliJMcp().createToolRegistry()

        val executor = simpleOpenAIExecutor(apiKey)
        val model = OpenAIModels.Chat.GPT4o

        val userPrompt = createUserPrompt()

        return AIAgent(
            id = "intellij-mcp-langfuse-agent",
            promptExecutor = executor,
            llmModel = model,
            toolRegistry = toolRegistry,
        ) {
            addLangfuseExporter()
            //addWeaveExporter()

        }.use { agent ->

            agent.run(
                "$userPrompt\n" +
                        "You can only call tools. Get it by calling IntelliJ MCP tools."
            )
        }
    }
}