package ai.koog.workshop.intro

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool


@Tool
@LLMDescription("Tool which does nothing but prints a message and returns response")
fun doNothingTool(message: String): String {
    println("Here is your message:\n${message}")
    println("Type your response:")
    val response = readln()
    return response
}