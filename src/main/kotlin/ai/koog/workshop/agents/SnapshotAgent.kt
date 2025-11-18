package ai.koog.workshop.agents

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.utils.io.use
import ai.koog.workshop.feature.createSnapshotDirectory
import ai.koog.workshop.feature.storeSnapshotInFile
import org.example.agents.calculator.strategy.CalculatorStrategy
import org.example.agents.calculator.tools.CalculatorTools

object SnapshotAgent {

    suspend fun runAgent(apiKey: String): String {

        val executor = simpleOpenAIExecutor(apiKey)
        val model = OpenAIModels.Chat.GPT4o

        val toolRegistry = ToolRegistry {
            tool(AskUser)
            tool(SayToUser)
            tools(CalculatorTools().asTools())
        }

        val userPrompt = "Calculate: 1+2+3+4"
        println("User prompt:\n$userPrompt\n")

        println("\nIntelliJ Agent is ready to work. Write your question: ")

        val snapshotAgentId = "snapshot-agent"
        val systemPrompt = "You are a calculator. " +
                "ALWAYS use tools to calculate!!! " +
                "Perform operations one by one! " +
                "Do not use run tools in parallel!"

        val snapshotDirectory = createSnapshotDirectory()

        //region Agent 1

        AIAgent(
            id = snapshotAgentId,
            strategy = CalculatorStrategy.snapshotStrategy,
            promptExecutor = executor,
            llmModel = model,
            toolRegistry = toolRegistry,
            systemPrompt = systemPrompt,
        ) {
            storeSnapshotInFile(snapshotDirectory)

        }.use { agent1 ->
            try {
                println("[Snapshot] Run agent 1.")
                val agent1Result = agent1.run(userPrompt)
                println("[Snapshot] Agent 1 result:\n$agent1Result\n")
            } catch (e: Exception) {
                println("[Snapshot] Agent 1 failed with exception: ${e.message}")
            }
        }

        //endregion Agent 1

        //region Agent 2

        val agent2Result = AIAgent(
            id = snapshotAgentId,
            strategy = CalculatorStrategy.snapshotStrategy,
            promptExecutor = executor,
            llmModel = model,
            toolRegistry = toolRegistry,
            systemPrompt = systemPrompt,
        ) {
            storeSnapshotInFile(snapshotDirectory)

        }.use { agent2 ->
            println("[Snapshot] Run agent 2.")
            val agent2Result = agent2.run(userPrompt)
            println("[Snapshot] Agent 2 result:\n$agent2Result\n")
            agent2Result
        }

        //endregion Agent 2

        return agent2Result
    }
}