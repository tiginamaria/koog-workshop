package ai.koog.workshop.intro

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.workshop.intro.utils.installDefaultEventHandler
import kotlinx.coroutines.runBlocking

// TODO:
//  1. Count number of tools provided by the MCP Playwright server
//  2. Try to modify prompt to call several tools
fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")

    // Tool registry from where the agent gets a lost of tool and selects the tool to execute
    // Start the Docker container with the Google Maps MCP server
    val process = ProcessBuilder(
        "npx",
        "@playwright/mcp@latest",
        "--port",
        "8932"
    ).start()

    // Wait for the server to start
    Thread.sleep(2000)

    try {
        runBlocking {
            println("Connecting to Playwright MCP server...")
            val toolRegistry = McpToolRegistryProvider.fromTransport(
                transport = McpToolRegistryProvider.defaultSseTransport("http://localhost:8931/sse")
            )
            println("Successfully connected to Playwright MCP server. Available tools:")
            toolRegistry.tools.forEach { println(it.descriptor) }
            val agent = AIAgent(
                promptExecutor = simpleOpenAIExecutor(token),
                toolRegistry = toolRegistry,
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

            agent.run("Open Bremen Constructur University page")
        }
    } catch (e: Exception) {
        println("Failed to connect to Playwright MCP server: ${e.message}")
    } finally {
        process.destroy()
    }
}