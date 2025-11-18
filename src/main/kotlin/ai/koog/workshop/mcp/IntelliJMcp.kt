package ai.koog.workshop.mcp

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.mcp.McpToolRegistryProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class IntelliJMcp {

    companion object {
        private const val IJ_MCP_SERVER_PORT = 64342
        private const val IJ_MCP_SERVER_URL = "http://localhost:$IJ_MCP_SERVER_PORT/sse"
    }

    private val retriableClient: HttpClient
        get() = HttpClient {
            install(HttpRequestRetry) {
                retryOnExceptionIf(maxRetries = 10) { _, cause ->
                    cause is IOException
                }
            }
        }

    suspend fun createToolRegistry(): ToolRegistry {
        println("Creating IntelliJ MCP tool registry...")
//        runIntelliJMcpProxyProcess()

        return McpToolRegistryProvider.fromTransport(
            transport = McpToolRegistryProvider.defaultSseTransport(IJ_MCP_SERVER_URL, retriableClient)
        )
    }

    //region Private Methods

    private suspend fun runIntelliJMcpProxyProcess(delay: Duration = 4.seconds): Process = withContext(Dispatchers.IO) {
        println("Starting IntelliJ MCP server process...")
        val builder = ProcessBuilder(
            "npx",
            "-y",
            "@jetbrains/mcp-proxy"
        ).redirectErrorStream(true)

        val process = builder.start()

        Runtime.getRuntime().addShutdownHook(Thread {
            process.destroy()
        })

        delay(delay)
        println("IntelliJ MCP server process started")
        process
    }

    //endregion Private Methods
}
