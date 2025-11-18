package ai.koog.workshop.intro

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import kotlinx.coroutines.runBlocking

// TODO:
//  On top of the previous agent add shop tools to add all the ingredients to the shopping list
fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")
    val shop = Shop()
    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(token),
        toolRegistry = ToolRegistry {
            TODO("Add shop tools")
        },
        agentConfig = AIAgentConfig(
            prompt = prompt("shop-prompt") {
                system("You are a helpful cooking assistant")
            },
            model = OpenAIModels.Chat.GPT4_1,
            maxAgentIterations = 100,
        ),
        strategy = strategy<Order, String>("strategy-name") {
            val nodePrompt by node<Order, String> {
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
    println("Result: $result")
    println("Shopping cart:")
    shop.cart.forEachIndexed { index, item ->
        println("${index + 1}. $item")
    }
}