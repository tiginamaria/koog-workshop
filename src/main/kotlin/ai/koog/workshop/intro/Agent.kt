package ai.koog.workshop.intro

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTool
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val agent = AIAgent(
            toolRegistry = ToolRegistry {
                // TODO: add tools
                tool(::doNothingTool.asTool())
            },
            strategy = strategy("my-strategy") {
                // TODO: implement strategy
                val myNode by node<String, String> { input ->
                    input
                }
                edge(nodeStart forwardTo myNode)
                edge(myNode forwardTo nodeFinish)
            },
            agentConfig = AIAgentConfig(
                // TODO: modify system prompt
                prompt = prompt("System") {
                    system("System prompt")
                },
                model = OpenAIModels.Chat.GPT4o,
                maxAgentIterations = 100
            ),
            promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))
        )

        // TODO: modify user prompt
        val result = agent.run("User prompt")
    }
}