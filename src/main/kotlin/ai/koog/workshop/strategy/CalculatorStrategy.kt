package org.example.agents.calculator.strategy

import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult

object CalculatorStrategy {
    private const val MAX_TOKENS_THRESHOLD = 1000

    val multipleToolCallsStrategy = strategy<String, String>("test") {
        val nodeCallLLM by nodeLLMRequestMultiple()
        val nodeExecuteTool by nodeExecuteMultipleTools()
        val nodeSendToolResult by nodeLLMSendMultipleToolResults()

        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeExecuteTool onMultipleToolCalls { true })
        edge(nodeExecuteTool forwardTo nodeSendToolResult)
        edge(nodeSendToolResult forwardTo nodeFinish onMultipleAssistantMessages { true } transformed { "Chat finished" })
        edge(nodeSendToolResult forwardTo nodeFinish onMultipleToolCalls { tc -> tc.any { tool -> tool.tool == "__exit__" }} transformed { "Chat finished" })
        edge(nodeSendToolResult forwardTo nodeExecuteTool onMultipleToolCalls { true })
    }

    val firstToolStrategy = strategy<String, String>("first-tool-strategy") {
        val nodeCallLLM by nodeLLMRequest()
        val nodeExecuteTool by nodeExecuteTool()
        val nodeSendToolResult by nodeLLMSendToolResult()

        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
        edge(nodeExecuteTool forwardTo nodeSendToolResult)
        edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeSendToolResult forwardTo nodeFinish onToolCall { tc -> tc.tool == "__exit__" } transformed { it.content })
        edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
    }

    val compressHistoryStrategy = strategy<String, String>("compress-history-strategy") {
        val nodeCallLLM by nodeLLMRequest()
        val nodeExecuteTool by nodeExecuteTool()
        val nodeSendToolResult by nodeLLMSendToolResult()
        val nodeCompressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
            name = "compress-history-node",
            strategy = HistoryCompressionStrategy.FromLastNMessages(3)
        )

        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
        edge(nodeExecuteTool forwardTo nodeCompressHistory onCondition { _ -> llm.readSession { prompt.messages.size > 10 } })
        edge(nodeCompressHistory forwardTo nodeSendToolResult)
        edge(nodeExecuteTool forwardTo nodeSendToolResult)
        edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeSendToolResult forwardTo nodeFinish onToolCall { tc -> tc.tool == "__exit__" } transformed { it.content })
        edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
    }

    var isFailed = false

    val snapshotStrategy = strategy<String, String>("snapshot-strategy") {
        val nodeCallLLM by nodeLLMRequest()
        val nodeExecuteTool by nodeExecuteTool()
        val nodeSendToolResult by nodeLLMSendToolResult()
        val nodeThrowException by node<ReceivedToolResult, ReceivedToolResult>(name = "node-throw-exception") { toolResult ->
            val result = toolResult.content.trim('"').toDoubleOrNull()
            println("[Throw] Node receive tool result: $result")
            if (!isFailed &&  result != null && result > 5.0) {
                println("[Throw] Node throw exception")
                isFailed = true
                throw IllegalStateException("Agent is going to be failed.")
            }
            println("[Throw] Node skip throw, return tool result")
            toolResult
        }

        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
        edge(nodeExecuteTool forwardTo nodeThrowException)
        edge(nodeThrowException forwardTo nodeSendToolResult)
        edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeSendToolResult forwardTo nodeFinish onToolCall { tc -> tc.tool == "__exit__" } transformed { it.content })
        edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
    }
}