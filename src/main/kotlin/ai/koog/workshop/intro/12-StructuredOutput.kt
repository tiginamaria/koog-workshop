package ai.koog.workshop.intro

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.structure.executeStructured
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@LLMDescription("The recipe for fo the dish")
@Serializable
data class Recipe(
    @param:LLMDescription("The name of the dish")
    val dish: String,
    @param:LLMDescription("List of ingredients required for the dish")
    val ingredients: List<String> = emptyList(),
    @param:LLMDescription("The instructions for preparing the dish")
    val instructions: String,
)

// TODO:
//  1. Set your system and user message, ask to generate a recipe for a dish
//  2. Create [Ingredient] data class with name and amount
fun main() {
    val token = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY is required.")
    val executor = simpleOpenAIExecutor(token)

    // Create a prompt with a system and a user message
    val prompt = prompt("my-prompt") {
        system("Your are a cooking assistant")
        user("How to make pizza")
    }
    prompt.messages.forEach { println(it) }

    runBlocking {
        val result = executor.executeStructured<Recipe>(
            prompt = prompt,
            model = OpenAIModels.Chat.GPT4_1,
            examples = listOf(
                Recipe(
                    dish = "pancakes",
                    ingredients = listOf("egg", "milk", "flour"),
                    instructions = "Mix all ingredients and cook"
                )
            )
        )
        val recipe = result.getOrNull()?.structure
        if (recipe != null) {
            println(recipe)
        } else {
            println("Error during structured request: ${result.exceptionOrNull()?.message}")
        }
    }
}