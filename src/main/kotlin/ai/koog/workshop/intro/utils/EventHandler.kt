package ai.koog.workshop.intro.utils

import ai.koog.agents.core.agent.GraphAIAgent
import ai.koog.agents.features.eventHandler.feature.EventHandler

fun GraphAIAgent.FeatureContext.installDefaultEventHandler() {
    install(EventHandler) {
        onAgentStarting {
            println("Agent started.")
        }
        onAgentCompleted {
            println("Agent completed.")
        }
        onNodeExecutionStarting {
            println("Node started: ${it.node.name}")
        }
        onNodeExecutionCompleted {
            println("Node completed: ${it.node.name}")
        }
        onAgentCompleted {
            println("Agent completed.")
        }
        onToolCallStarting {
            println("Tool call started: ${it.tool.name} with arguments: ${it.toolArgs}")
        }
        onToolCallCompleted {
            println("Tool call completed: ${it.tool.name} with result: ${it.result}")
        }
    }
}