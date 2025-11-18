package org.example.agents.calculator

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.utils.io.use
import ai.koog.workshop.feature.addConsoleOutput
import org.example.agents.calculator.strategy.CalculatorStrategy
import org.example.agents.calculator.tools.CalculatorTools

object CalculatorAgent {

    fun createAgent(openAiKey: String, agentId: String? = null): AIAgent<String, String> {

        val toolRegistry = ToolRegistry {
            tool(AskUser)
            tool(SayToUser)
            tools(CalculatorTools().asTools())
        }

        return AIAgent(
            id = agentId,
            strategy = CalculatorStrategy.firstToolStrategy,
            promptExecutor = simpleOpenAIExecutor(openAiKey),
            llmModel = OpenAIModels.Chat.GPT4o,
            toolRegistry = toolRegistry,
            systemPrompt = "You are a calculator. ALWAYS use tools to calculate!!!",
        )
    }

    suspend fun runAgent(apiKey: String) {
        val executor = simpleOpenAIExecutor(apiKey)
        val model = OpenAIModels.Chat.GPT4o

        val toolRegistry = ToolRegistry {
            tool(AskUser)
            tool(SayToUser)
            tools(CalculatorTools().asTools())
        }

        val userPrompt = "Calculate: 2+3-4+5"
        println("User prompt:\n$userPrompt\n")

        println("\nIntelliJ Agent is ready to work. Write your question: ")

        return AIAgent(
            id = "calculator-agent",
            strategy = CalculatorStrategy.multipleToolCallsStrategy,
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