package ai.koog.workshop.intro

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import kotlinx.serialization.Serializable

@Serializable
data class Message(val text: String)


@Tool
@LLMDescription("Tool which does nothing but print a message and return user response")
fun doNothingTool(message: Message): String {
    println(message.text)
    val response = readln()
    return response
}