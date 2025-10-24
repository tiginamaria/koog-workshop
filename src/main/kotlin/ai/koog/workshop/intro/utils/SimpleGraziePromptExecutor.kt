package ai.koog.workshop.intro.utils

import ai.jetbrains.code.prompt.executor.clients.grazie.koog.createGraziePromptExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import kotlinx.coroutines.runBlocking


fun simpleGraziePromptExecutor(token: String): PromptExecutor = runBlocking {
    createGraziePromptExecutor(token)
}
