package ai.koog.workshop.feature

import ai.koog.agents.core.agent.GraphAIAgent.FeatureContext
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.agents.snapshot.feature.Persistence
import ai.koog.agents.snapshot.providers.file.JVMFilePersistenceStorageProvider
import ai.koog.prompt.message.Message
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.notExists

fun FeatureContext.storeSnapshotInFile(root: Path) {

    install(Persistence) {
        storage = JVMFilePersistenceStorageProvider(root)
        enableAutomaticPersistence = true
    }

    install(EventHandler) {
        onNodeExecutionStarting { eventContext ->
            if (eventContext.node.name == "__start__") {
                println("[EH] Agent '${eventContext.context.agentId}' call __start__ node")
            }

            println("[EH] starting node: ${eventContext.node.name}, last [T] message: ${eventContext.context.llm.prompt.messages.findLast { it is Message.Tool.Call }?.content }")
        }
    }
}

fun createSnapshotDirectory(): Path {
    val currentFile = Path.of(".").absolute()
    val resourcesDirectory = currentFile
        .resolve("src")
        .resolve("main")
        .resolve("resources")

    val snapshotsDirectory = resourcesDirectory
        .resolve("snapshots")

    if (snapshotsDirectory.notExists()) {
        snapshotsDirectory.toFile().mkdirs()
    }

    println("[Persistency] Snapshot directory: $snapshotsDirectory")

    return snapshotsDirectory
}
