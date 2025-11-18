package ai.koog.workshop.intro

import ai.jetbrains.code.prompt.llm.JetBrainsAIModels
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.dsl.prompt
import ai.koog.workshop.intro.utils.simpleGraziePromptExecutor
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking

/**
 * [ToolSet] in the way to group tools
 */
class Shop : ToolSet {

    private val logger = KotlinLogging.logger {}

    /** The cart is a set of items */
    val cart = mutableSetOf<String>()

    /**
     * Adds an item to the cart, returns a message if the item has been added or already exists in the cart
     */
    @Tool("add_to_cart")
    @LLMDescription("Add an item to the cart.")
    fun addToCart(
        @LLMDescription("The item to add to the cart.")
        item: String
    ): String {
        logger.info { "Adding `$item` to cart" }
        if (cart.add(item)) {
            return "Item `$item` has been added to your cart."
        }
        return "Item `$item` is already in your cart."
    }

    /**
     * Gets the price of an item (mocked, price is always 100)
     */
    @Tool("get_price")
    @LLMDescription("Get the price of an item.")
    fun getPrice(
        @LLMDescription("The item name to get the price of.")
        item: String,
        @LLMDescription("The discount percentage. Default is 0")
        discount: Int = 0
    ): Int {
        logger.info { "Get price of `$item` with discount `$discount`" }
        val price = 100 * (100 - discount) / 100
        logger.info { "The price of `$item` is `$price`" }
        return price
    }
}

// TODO:
//  1. Annotate `addToCart` tool with @Tool and @LLMDescription
//  2. Make sure all tools are in the shop tool set
//  3. Add tools to executor
//  4. Ask llm about the price of the item
//  5. Ask llm about the price of the item and add it to the cart, allow parallel tool calls
//  6. Mention the discount
fun main() {
    val token = System.getenv("GRAZIE_TOKEN") ?: error("GRAZIE_TOKEN is required.")
    val executor = simpleGraziePromptExecutor(token)

    val shop = Shop()
    shop.asTools().forEach {
        println("Tool name: ${it.name}")
        println("Tool description: ${it.description}")
        println("Tool description: ${it.descriptor}")
    }

    val prompt = prompt("shop-prompt") {
        system("You are a helpful shopping assistant")
        // Add a user message
        user("Get price of the laptop and add it to the cart")
    }

    runBlocking {
        val result = executor.execute(
            prompt = prompt,
            model = JetBrainsAIModels.OpenAI_GPT4_1_via_JBAI,
            tools = shop.asTools().map { it.descriptor }
        )

        println(result)
    }
}