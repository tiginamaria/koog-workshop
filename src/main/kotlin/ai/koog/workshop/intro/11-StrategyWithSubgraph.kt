package ai.koog.workshop.intro

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import kotlinx.coroutines.runBlocking

// TODO:
//  Replace strategy from the previous agent with [subgraphWithTask]
fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")
    val shop = Shop()
    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(token),
        toolRegistry = ToolRegistry {
            tools(shop.asTools())
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
                "Order all ingredients to cook ${it.dish} for ${it.guests} guests with ${it.allergies.ifEmpty { "no" }} allergies?"
            }
            val nodeLLMRequest by nodeLLMRequest()
            val nodeExecuteTool by nodeExecuteTool()
            val nodeSendToolResult by nodeLLMSendToolResult()

            edge(nodeStart forwardTo nodePrompt)
            edge(nodePrompt forwardTo nodeLLMRequest)
            edge(nodeLLMRequest forwardTo nodeExecuteTool onToolCall { true })
            edge(nodeExecuteTool forwardTo nodeSendToolResult)
            edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
            edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
            edge(nodeLLMRequest forwardTo nodeFinish onAssistantMessage { true })
        }
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