package ai.koog.workshop.feature

import ai.koog.agents.core.agent.GraphAIAgent.FeatureContext
import ai.koog.agents.core.feature.message.FeatureMessageProcessor
import ai.koog.agents.features.tracing.feature.Tracing
import ai.koog.agents.features.tracing.writer.TraceFeatureMessageFileWriter
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.notExists
import kotlin.io.path.pathString

fun FeatureContext.addTracingToFile(writer: FeatureMessageProcessor) {
    install(Tracing) {
        addMessageProcessor(writer)
    }
}

fun createLogWriter(): FeatureMessageProcessor {
    val currentFile = Path.of(".").absolute()
    val logDirectory = currentFile
        .resolve("src")
        .resolve("main")
        .resolve("resources")
        .resolve("log")

    if (logDirectory.notExists()) {
        logDirectory.toFile().mkdirs()
    }

    val logFile = logDirectory.resolve("trace.log").toAbsolutePath()

    println("[Tracing] Log file path: $logFile")

    return TraceFeatureMessageFileWriter(
        logFile,
        { path: Path -> SystemFileSystem.sink(path = kotlinx.io.files.Path(path.pathString)).buffered() }
    )
}