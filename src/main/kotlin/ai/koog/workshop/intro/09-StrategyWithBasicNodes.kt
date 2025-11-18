package ai.koog.workshop.intro

import ai.jetbrains.code.prompt.llm.JetBrainsAIModels
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import ai.koog.workshop.intro.utils.simpleGraziePromptExecutor
import kotlinx.coroutines.runBlocking

/**
 * Data class representing an order
 */
data class Order(val dish: String, val guests: Int, val allergies: List<String> = emptyList())

// TODO:
//  Create a strategy which asks llm what the ingredients are required for the given dish and returns the description
fun main() {
    val token = System.getenv("GRAZIE_TOKEN") ?: error("GRAZIE_TOKEN is required.")

    val agent = AIAgent(
        promptExecutor = simpleGraziePromptExecutor(token),
        toolRegistry = ToolRegistry {

        },
        agentConfig = AIAgentConfig(
            prompt = prompt("shop-prompt") {
                system("You are a helpful cooking assistant")
            },
            model = JetBrainsAIModels.OpenAI_GPT4_1_via_JBAI,
            maxAgentIterations = 10,
        ),
        strategy = strategy<Order, String>("strategy-name") {
            val nodePrompt by node<Order, String>("node-prompt") {
                "Which ingredients are required to cook ${it.dish} for ${it.guests} guests with ${it.allergies.ifEmpty { "no" }} allergies?"
            }
            val nodeRequestLLM by nodeLLMRequest()
            edge(nodeStart forwardTo nodePrompt)
            edge(nodePrompt forwardTo nodeRequestLLM)
            edge(nodeRequestLLM forwardTo nodeFinish onAssistantMessage { true })
        },
    ) {
        // Same as in SimpleAgent
        installDefaultEventHandler()
    }

    val result = runBlocking {
        // Send your question to the agent
        agent.run(Order("pizza", 8, listOf("gluten")))
    }
    println(result)
}