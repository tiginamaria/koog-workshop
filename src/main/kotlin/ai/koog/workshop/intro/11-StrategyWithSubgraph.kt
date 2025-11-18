package ai.koog.workshop.intro

import ai.jetbrains.code.prompt.llm.JetBrainsAIModels
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.context.agentInput
import ai.koog.agents.core.agent.entity.AIAgentStorageKey
import ai.koog.agents.core.agent.entity.ToolSelectionStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.agent.subgraphWithTask
import ai.koog.prompt.dsl.prompt
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import ai.koog.workshop.intro.utils.simpleGraziePromptExecutor
import kotlinx.coroutines.runBlocking

// TODO:
//  Replace strategy from the previous agent with [subgraphWithTask]
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
        strategy = strategy<Order, String>("strategy-name") {
            // First subgraph
            val subgraphPrice by subgraphWithTask<Order, Unit>(toolSelectionStrategy = ToolSelectionStrategy.ALL) { order ->
                "Order all ingredients to cook ${order.dish} for ${order.guests} guests with ${order.allergies.ifEmpty { "no" }} allergies?" +
                        "Ask the price of all ingredients"
            }

            // Second subgraph
            val subgraphAdd by subgraphWithTask<Unit, Unit>(toolSelectionStrategy = ToolSelectionStrategy.ALL) { order ->
                "Add all ingredients to the shopping cart."
            }

            edge(nodeStart forwardTo subgraphPrice)
            edge(subgraphPrice forwardTo subgraphAdd)
            edge(subgraphAdd forwardTo nodeFinish transformed { "" })

            // Example of how to clean / modify the prompt
            val nodeRewritePrompt by node<Unit, Unit> {
                llm.writeSession {
                    rewritePrompt { oldPrompt ->
                        prompt("new-prompt") {
                            oldPrompt.messages.take(5)
                        }
                    }
                }
            }

            // Example of how to get agent intput from any node inside the graph
            val nodeGetAgentInput by node<Unit, Unit> {
                val input: Order = agentInput<Order>()
            }

            // Storage key
            val key = AIAgentStorageKey<Recipe>("recipe")

            // Node to put value to storage
            val nodePutToStorage by node<Unit, Unit> {
                storage.set(
                    key, Recipe(
                        dish = "omlet",
                        ingredients = listOf(Ingredient("egg", "1")),
                        instructions = "Mix all"
                    )
                )
            }

            // Node to get value from storage
            val nodeGetFromStorage by node<Unit, Unit> {
                val recipe = storage.get(key)
            }
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