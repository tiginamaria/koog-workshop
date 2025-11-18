package ai.koog.workshop.intro

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.asTool
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking
import kotlin.random.Random


/**
 * Gets the price of an item (mocked, returns a random number)
 */
// You can define the tool custom name, otherwise the name of the function will be used
@Tool("get_price")
// Description of the tool, it will be used by the LLM to know what to call
@LLMDescription("Get the price of an item.")
fun getPrice(
    // Description of the tool argument, it will be used by the LLM to know what arguments to provide
    @LLMDescription("The item name to get the price of.")
    item: String,
): Int {
    return Random.nextInt(100)
}

// TODO:
//  1. Ask llm about the price of the item and add tool get_price to the request
//  2. Look at the structure of the llm response? How it is different from the text reply?
//  3. Add discount argument to the getPrice tool with default value. How tool descriptor will change?
fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")
    val executor = simpleOpenAIExecutor(token)

    val prompt = prompt("shop-prompt") {
        system("You are a helpful shopping assistant")
        // Add a user message
        user("Your question to shopping assistant")
    }

    val tool = ::getPrice.asTool()
    // Name of the tool
    println("Tool name: ${tool.name}")
    // Description of the tool
    println("Tool description: ${tool.description}")
    // Descriptor of the tool, the llm instruction how to call this tool and what arguments to provide
    println("Tool descriptor: ${tool.descriptor}")

    runBlocking {
        val result = executor.execute(
            prompt = prompt,
            model = OpenAIModels.Chat.GPT4_1,
            tools = listOf(tool.descriptor)
        )

        println(result)
    }
}