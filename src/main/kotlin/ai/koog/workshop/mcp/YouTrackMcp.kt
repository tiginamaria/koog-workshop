package ai.koog.workshop.mcp

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.agents.mcp.defaultStdioTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class YouTrackMcpMcp {

    companion object {
        private const val YOUTRACK_API_TOKEN_NAME = "YOUTRACK_API_TOKEN"
        private const val YOUTRACK_MCP_SERVER_URL = "https://youtrack.jetbrains.com/mcp"
    }

    /**
     * Starts the YouTrack MCP server.
     */
    val mcpProcess: Process by lazy {
        val processBuilder = ProcessBuilder(
            "npx",
            "mcp-remote",
            YOUTRACK_MCP_SERVER_URL,
            "--header",
            "Authorization:$\\{AUTH_HEADER\\}",
        )

        processBuilder.environment()["AUTH_HEADER"] = "Bearer ${System.getenv(YOUTRACK_API_TOKEN_NAME)}"

        val process = processBuilder.start()

        // Wait for the server to start
        Thread.sleep(2000)

        process
    }

    suspend fun runProcess(): Process = withContext(Dispatchers.IO) {
        val builder = ProcessBuilder(
            "npx",
            "mcp-remote",
            YOUTRACK_MCP_SERVER_URL,
            "--debug",
            "--header",
            "Authorization: Bearer ${System.getenv(YOUTRACK_API_TOKEN_NAME)}",
        )

        builder.redirectErrorStream(true)

        val process = builder.start()

        delay(2000)
        process
    }

    /**
     * Creates and initializes a tool registry with the YouTrack MCP server using the default SSE transport.
     *
     * @return An instance of [ToolRegistry] configured with the default SSE transport.
     */
    suspend fun createToolRegistry(mcpProcess: Process): ToolRegistry {
        val toolRegistry = McpToolRegistryProvider.fromTransport(
            transport = McpToolRegistryProvider.defaultStdioTransport(mcpProcess)
        )

        return toolRegistry
    }
}
