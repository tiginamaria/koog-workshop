package ai.koog.workshop.intro

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/**
 * [ToolSet] in the way to group tools
 */
class Shop : ToolSet {
    /** The cart is a set of items */
    val cart = mutableSetOf<String>()

    /**
     * Adds an item to the cart, returns a message if the item has been added or already exists in the cart
     */
    fun addToCart(item: String): String {
        if (cart.add(item)) {
            return "Item $item has been added to your cart."
        }
        return "Item $item is already in your cart."
    }

    /**
     * Gets the price of an item (mocked, returns a random number)
     */
    @Tool("get_price")
    @LLMDescription("Get the price of an item.")
    fun getPrice(item: String): Int {
        return Random.nextInt(100)
    }
}

// TODO:
//  1. Annotate `addToCart` with @Tool and @LLMDescription
//  2. Make sure all tools are in the shop tool set
//  3. Add tools to executor and ask the price of the item and add it to the cart

fun main() {
    val shop = Shop()
    shop.asTools().forEach {
        println("Tool name: ${it.name}")
        println("Tool description: ${it.description}")
        println("Tool description: ${it.descriptor}")
    }

    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")
    val executor = simpleOpenAIExecutor(token)
    val prompt = prompt("shop-prompt") {
        system("You are a helpful shopping assistant")
        // Add a user message
        user("Your question to shopping assistant")
    }

    runBlocking {
        val result = executor.execute(
            prompt = prompt,
            model = OpenAIModels.Chat.GPT4_1,
            tools = shop.asTools().map { it.descriptor }
        )

        println(result)
    }
}