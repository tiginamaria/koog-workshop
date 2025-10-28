package ai.koog.workshop.intro

import ai.koog.prompt.dsl.prompt
import kotlinx.io.files.Path

// TODO:
//  1. Create prompt with different types of messages
//  2. Print the prompt, make sure all the defined messages are there
fun main() {
    val resourcePath =
        object {}.javaClass.classLoader.getResource("images")?.path ?: error("images directory not found")

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
            call(id = "your-tool-id", tool = "Your tool name", content = "Your tool content")
            result(id = "your-tool-id", tool = "Your tool name", content = "Your tool result")
        }
        // You can also add attachments to the prompt
        user {
            +"Guess what is in the picture?"
            attachments {
                image(Path("$resourcePath/img.png"))
            }
        }
    }
    prompt.messages.forEach { println(it) }
}