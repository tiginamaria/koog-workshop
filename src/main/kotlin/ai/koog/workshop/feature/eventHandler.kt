package ai.koog.workshop.feature

import ai.koog.agents.core.agent.GraphAIAgent.FeatureContext
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.agents.features.tracing.traceString

fun FeatureContext.addConsoleOutput() {
    handleEvents {
        onAgentStarting { eventContext ->
            println(
                "[EH] OnAgentStarting (agent id: ${eventContext.agent.id}, run id: ${eventContext.runId})"
            )
        }

        onAgentCompleted { eventContext ->
            println(
                "[EH] OnAgentCompleted (agent id: ${eventContext.agentId}, run id: ${eventContext.runId}, result: ${eventContext.result})"
            )
        }

        onAgentExecutionFailed { eventContext ->
            println(
                "[EH] OnAgentExecutionFailed (agent id: ${eventContext.agentId}, run id: ${eventContext.runId}, error: ${eventContext.throwable.message})"
            )
        }

        onAgentClosing { eventContext ->
            println(
                "[EH] OnAgentClosing (agent id: ${eventContext.agentId})"
            )
        }

        onStrategyStarting { eventContext ->
            println(
                "[EH] OnStrategyStarting (run id: ${eventContext.context.runId}, strategy: ${eventContext.strategy.name})"
            )
        }

        onStrategyCompleted { eventContext ->
            println(
                "[EH] OnStrategyCompleted (run id: ${eventContext.context.runId}, strategy: ${eventContext.strategy.name}, result: ${eventContext.result})"
            )
        }

        onNodeExecutionStarting { eventContext ->
            println(
                "[EH] OnNodeExecutionStarting (run id: ${eventContext.context.runId}, node: ${eventContext.node.name}, input: ${eventContext.input})"
            )
        }

        onNodeExecutionCompleted { eventContext ->
            println(
                "[EH] OnNodeExecutionCompleted (run id: ${eventContext.context.runId}, node: ${eventContext.node.name}, input: ${eventContext.input}, output: ${eventContext.output})"
            )
        }

        onNodeExecutionFailed { eventContext ->
            println(
                "[EH] OnNodeExecutionFailed (run id: ${eventContext.context.runId}, node: ${eventContext.node.name}, input: ${eventContext.input}, error: ${eventContext.throwable.message})"
            )
        }

        onSubgraphExecutionStarting { eventContext ->
            println(
                "[EH] OnSubgraphExecutionStarting (run id: ${eventContext.context.runId}, subgraph: ${eventContext.subgraph.name}, input: ${eventContext.input})"
            )
        }

        onSubgraphExecutionCompleted { eventContext ->
            println(
                "[EH] OnSubgraphExecutionCompleted (run id: ${eventContext.context.runId}, subgraph: ${eventContext.subgraph.name}, input: ${eventContext.input}, output: ${eventContext.output})"
            )
        }

        onSubgraphExecutionFailed { eventContext ->
            println(
                "[EH] OnSubgraphExecutionFailed (run id: ${eventContext.context.runId}, subgraph: ${eventContext.subgraph.name}, input: ${eventContext.input}, error: ${eventContext.throwable.message})"
            )
        }

        onLLMCallStarting { eventContext ->
            println(
                "[EH] OnLLMCallStarting (run id: ${eventContext.runId}, prompt: ${eventContext.prompt.traceString}, tools: [${
                    eventContext.tools.joinToString {
                        it.name
                    }
                }])"
            )
        }

        onLLMCallCompleted { eventContext ->
            println(
                "[EH] OnLLMCallCompleted (run id: ${eventContext.runId}, prompt: ${eventContext.prompt.traceString}, model: ${eventContext.model.id}, tools: [${
                    eventContext.tools.joinToString {
                        it.name
                    }
                }], responses: [${eventContext.responses.joinToString { response -> response.traceString }}], tokens spent: ${eventContext.responses.sumOf { response -> response.metaInfo.totalTokensCount ?: 0 }})"
            )
        }

        onToolCallStarting { eventContext ->
            println(
                "[EH] OnToolCallStarting (run id: ${eventContext.runId}, tool: ${eventContext.tool.name}, args: ${eventContext.toolArgs})"
            )
        }

        onToolValidationFailed { eventContext ->
            println(
                "[EH] OnToolValidationFailed (run id: ${eventContext.runId}, tool: ${eventContext.tool.name}, args: ${eventContext.toolArgs}, value: ${eventContext.error})"
            )
        }

        onToolCallFailed { eventContext ->
            println(
                "[EH] OnToolCallFailed (run id: ${eventContext.runId}, tool: ${eventContext.tool.name}, args: ${eventContext.toolArgs}, throwable: ${eventContext.throwable.message})"
            )
        }

        onToolCallCompleted { eventContext ->
            println(
                "[EH] OnToolCallCompleted (run id: ${eventContext.runId}, tool: ${eventContext.tool.name}, args: ${eventContext.toolArgs}, result: ${eventContext.result})"
            )
        }

        onLLMStreamingStarting { eventContext ->
            println(
                "[EH] OnLLMStreamingStarting (run id: ${eventContext.runId}, prompt: ${eventContext.prompt.traceString}, model: ${eventContext.model.id}, tools: [${
                    eventContext.tools.joinToString { it.name }
                }])"
            )
        }

        onLLMStreamingFrameReceived { eventContext ->
            println(
                "[EH] OnLLMStreamingFrameReceived (run id: ${eventContext.runId}, frame: ${eventContext.streamFrame})"
            )
        }

        onLLMStreamingFailed { eventContext ->
            println(
                "[EH] OnLLMStreamingFailed (run id: ${eventContext.runId}, error: ${eventContext.error.message})"
            )
        }

        onLLMStreamingCompleted { eventContext ->
            println(
                "[EH] OnLLMStreamingCompleted (run id: ${eventContext.runId}, prompt: ${eventContext.prompt.traceString}, model: ${eventContext.model.id}, tools: [${
                    eventContext.tools.joinToString { it.name }
                }])"
            )
        }
    }
}

fun FeatureContext.addTokensOutput() {
    handleEvents {

        onLLMCallCompleted { eventContext ->
            println(
                "[EH] OnLLMCallCompleted (last prompt: ${eventContext.prompt.messages.last().content}, model: ${eventContext.model.id}, tools: [${
                    eventContext.tools.joinToString {
                        it.name
                    }
                }], responses: [${eventContext.responses.joinToString { response -> response.traceString }}])\nTokens spent: ${eventContext.responses.sumOf { response -> response.metaInfo.totalTokensCount ?: 0 }}"
            )
        }
    }
}