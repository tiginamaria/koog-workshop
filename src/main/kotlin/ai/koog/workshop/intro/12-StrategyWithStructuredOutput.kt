package ai.koog.workshop.intro

import ai.jetbrains.code.prompt.llm.JetBrainsAIModels
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.entity.ToolSelectionStrategy
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.agent.subgraphWithTask
import ai.koog.prompt.dsl.prompt
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import ai.koog.workshop.intro.utils.simpleGraziePromptExecutor
import kotlinx.coroutines.runBlocking

// TODO:
//  On top of the previous agent, make agent return a recipe for the given order
fun main() {
    val token = System.getenv("GRAZIE_TOKEN") ?: error("GRAZIE_TOKEN is required.")
    val shop = Shop()
    val agent = AIAgent(
        promptExecutor = simpleGraziePromptExecutor(token),
        toolRegistry = ToolRegistry {
            tools(shop.asTools())
        },
        agentConfig = AIAgentConfig(
            prompt = prompt("shop-prompt") {
                system("You are a helpful cooking assistant")
            },
            model = JetBrainsAIModels.OpenAI_GPT4_1_via_JBAI,
            maxAgentIterations = 100,
        ),
        strategy = strategy<Order, Unit>("strategy-name") {
            val orderSubgraph by subgraphWithTask<Order, Unit>(
                toolSelectionStrategy = ToolSelectionStrategy.ALL,
            ) {
                "Order all ingredients to cook ${it.dish} for ${it.guests} guests with ${it.allergies.ifEmpty { "no" }} allergies?"
            }

            nodeStart then orderSubgraph then nodeFinish
        },
    ) {
        // Same as in SimpleAgent
        installDefaultEventHandler()
    }

    val result = runBlocking {
        // Send your question to the agent
        agent.run(Order("pizza", 8, listOf("gluten")))
    }
    println("Recipe: $result")
    println("Shopping cart:")
    shop.cart.forEachIndexed { index, item ->
        println("${index + 1}. $item")
    }
}