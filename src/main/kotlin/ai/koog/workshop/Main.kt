package ai.koog.workshop

import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.workshop.agents.CompressHistoryAgent
import ai.koog.workshop.agents.IntelliJAgent
import ai.koog.workshop.agents.IntelliJAgentTracingInFile
import ai.koog.workshop.agents.IntelliJAgentWithEventHandler
import ai.koog.workshop.agents.IntelliJOpenTelemetryAgent
import ai.koog.workshop.agents.SnapshotAgent
import kotlinx.coroutines.runBlocking
import org.example.agents.calculator.CalculatorAgent

fun main() = runBlocking {

    val openAiApiKey = System.getenv("OPENAI_API_KEY") ?: error("'OPENAI_API_KEY' is not defined")

    //region MCP

    // 1. MCP Agent
//    println("--- 1. MCP Agent ---")
//    val agentResult = IntelliJAgent.runAgent(apiKey = openAiApiKey)
//    println("MCP Agent result:\n$agentResult\n")

    //endregion MCP

    //region Tracing

    // 2. Event Handler
//    println("--- 2. MCP Agent with console output ---")
//    val agentResultWithConsoleOutput = IntelliJAgentWithEventHandler.runAgent(apiKey = openAiApiKey)
//    println("MCP Agent result with console output:\n$agentResultWithConsoleOutput\n")

    // 3. Tracing in File
//    println("--- 3. MCP Agent Tracing To File ---")
//    val agentResultTracingInFile = IntelliJAgentTracingInFile.runAgent(apiKey = openAiApiKey)
//    println("MCP Agent result with Tracing in file:\n$agentResultTracingInFile\n")

    // 4. Open Telemetry
//    println("--- 4. MCP Agent with Open Telemetry exporters ---")
//    val agentResultWithOpenTelemetry = IntelliJOpenTelemetryAgent.runAgent(apiKey = openAiApiKey)
//    println("MCP Agent result with Open Telemetry exporters:\n$agentResultWithOpenTelemetry\n")

    // 5. AI Debugger
//    println("--- 5. MCP Agent AI Debugger ---")
//    val aiDebuggerAgentResult = CalculatorAgent.runAgent(apiKey = openAiApiKey)
//    println("MCP Agent result AI Debugger:\n$aiDebuggerAgentResult\n")

    //endregion Tracing

    //region Compression

    // 6. Compress History
//    println("--- 6. Compress History agent ---")
//    val agentCompressHistoryResult = CompressHistoryAgent.runAgent(apiKey = openAiApiKey)
//    println("Compress History agent result:\n$agentCompressHistoryResult\n")

    //endregion Compression

    //region Snapshot

    // 7. Snapshot
    println("--- 7. Snapshot agent ---")
    val agentWithSnapshotResult = SnapshotAgent.runAgent(apiKey = openAiApiKey)
    println("Agent with Snapshot result:\n$agentWithSnapshotResult\n")

    //endregion Snapshot
}