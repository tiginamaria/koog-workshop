package ai.koog.workshop.intro

import ai.koog.agents.mcp.McpToolRegistryProvider
import kotlinx.coroutines.runBlocking

// TODO:
//  1. Count number of tools provided by the MCP Playwright server
fun main() {
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
            try {
                // Create the ToolRegistry with tools from the MCP server
                println("Connecting to Playwright MCP server...")
                val toolRegistry = McpToolRegistryProvider.fromTransport(
                    transport = McpToolRegistryProvider.defaultSseTransport("http://localhost:8931/sse")
                )
                println("Successfully connected to Playwright MCP server. Available tools:")
                toolRegistry.tools.forEach { println(it.descriptor) }
            } catch (e: Exception) {
                println("Error connecting to Playwright MCP server: ${e.message}")
                e.printStackTrace()
            }
        }
    } catch (e: Exception) {
        println("Failed to connect to Playwright MCP server: ${e.message}")
    } finally {
        process.destroy()
    }
}