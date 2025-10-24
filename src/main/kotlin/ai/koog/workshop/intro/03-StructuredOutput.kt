package ai.koog.workshop.intro

import ai.jetbrains.code.prompt.llm.JetBrainsAIModels
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.structure.executeStructured
import ai.koog.workshop.intro.utils.simpleGraziePromptExecutor
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@LLMDescription("The recipe for fo the dish")
@Serializable
data class Recipe(
    @param:LLMDescription("The name of the dish")
    val dish: String,
    @param:LLMDescription("List of ingredients required for the dish")
    val ingredients: List<String>,
    @param:LLMDescription("The instructions for preparing the dish")
    val instructions: String,
)

// TODO:
//  1. Set your system and user message, ask to generate a recipe for a dish
//  2. Create [Ingredient] data class with name and amount
fun main() {
    val token = System.getenv("GRAZIE_TOKEN") ?: error("GRAZIE_TOKEN is required.")
    val executor = simpleGraziePromptExecutor(token)

    // Create a prompt with a system and a user message
    val prompt = prompt("my-prompt") {
        system("Your are a cooking assistant")
        user("How to make pizza")
    }
    prompt.messages.forEach { println(it) }

    runBlocking {
        val result = executor.executeStructured<Recipe>(
            prompt = prompt,
            model = JetBrainsAIModels.OpenAI_GPT4_1_via_JBAI,
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