package ai.koog.workshop.intro

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import kotlinx.coroutines.runBlocking

// TODO:
//  1. Create an instance of the shop
//  2. Create an agent with OpenAIPromptExecutor GPT-4o model, shop tools and singleRunStrategy
//  3. Run the agent asking the price of the product, print the result
//  4. Run the agent asking to put the product into the cart, print the result and content of the cart
//  5. Run the agent asking something complex or not relevant to the shop, print the result
fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")
    val shop = Shop()

    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(token),
        toolRegistry = ToolRegistry {
            // Register shop tools
            tools(shop.asTools())
        },
        agentConfig = AIAgentConfig(
            // Initial prompt to user for agent
            prompt = prompt("shop-prompt") {
                system("You are a helpful shopping assistant")
            },
            // Model to use for the agent, should match one of the models supported by the llm provider in prompt executor
            model = OpenAIModels.Chat.GPT4_1,
            // Max number of agent steps to execute before force stop
            maxAgentIterations = 10,
        ),
        // The logic of the agent
        strategy = singleRunStrategy(),
    ) {
        installDefaultEventHandler()
    }

    val result = runBlocking {
        // Send your question to the agent
        agent.run("What is the price of the banana?")
    }
    println(result)

    println("Shopping cart:")
    shop.cart.forEachIndexed { index, item ->
        println("${index + 1}. $item")
    }
}