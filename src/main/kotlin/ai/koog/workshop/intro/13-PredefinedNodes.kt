package ai.koog.workshop.intro

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.context.agentInput
import ai.koog.agents.core.agent.entity.AIAgentStorageKey
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMCompressHistory
import ai.koog.agents.core.dsl.extension.nodeLLMRequestStructured
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import kotlinx.coroutines.runBlocking

fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")

    val agent = AIAgent(
        promptExecutor = simpleOpenAIExecutor(token),
        toolRegistry = ToolRegistry {},
        agentConfig = AIAgentConfig(
            prompt = prompt("shop-prompt") {
                system("You are a helpful cooking assistant")
            },
            model = OpenAIModels.Chat.GPT4_1,
            maxAgentIterations = 10,
        ),
        strategy = strategy<Order, String>("strategy-name") {
            // Example of how to get a structured response from LLM
            val nodeRequestStructured by nodeLLMRequestStructured<Recipe>()

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

            // Compression history of the agent
            val nodeCompressionHistory by nodeLLMCompressHistory<String>()

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
                        instructions = "Mix all"
                    )
                )
            }

            // Node to get value from storage
            val nodeGetFromStorage by node<Unit, Unit> {
                val recipe = storage.get(key)
            }
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