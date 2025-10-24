package ai.koog.workshop.intro

import ai.koog.prompt.dsl.prompt

// TODO: create prompt with system and user message, print the prompt
fun main() {
    // Prompt is a list of messages to be sent to the llm as well as the llm parameters
    val prompt = prompt("my-prompt") {
        // System message should contain general instruction for the agent
        // e.x You are a helpful assistant
        system("Your system message")
        // User message should contain the user's question or instruction for the agent
        user("Your user message")
        // Except from system and user messages, prompt can also contain assistant, tool call or tool result messages
        assistant("Your assistant message")
        tool {
            call(id="your-tool-id", tool="Your tool name", content="Your tool content")
            result(id="your-tool-id", tool="Your tool name", content="Your tool result")
        }
    }
    prompt.messages.forEach { println(it) }
}