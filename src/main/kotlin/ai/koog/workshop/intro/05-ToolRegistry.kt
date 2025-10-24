package ai.koog.workshop.intro

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTool

// TODO: create a tool registry and register a tool
fun main() {
    // Tool registry from where the agent gets a lost of tool and selects the tool to execute
    val toolRegistry = ToolRegistry {
        // Register tool
        tool(::getPrice.asTool())
    }

    // Get tool from a registry
    val tool = toolRegistry.getTool("get_price")
    println("Tool descriptor: ${tool.descriptor}")
}