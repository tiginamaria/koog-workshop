package ai.koog.workshop.feature

import ai.koog.agents.core.agent.GraphAIAgent.FeatureContext
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.agents.features.opentelemetry.integration.langfuse.addLangfuseExporter
import ai.koog.agents.features.opentelemetry.integration.weave.addWeaveExporter

fun FeatureContext.addLangfuseExporter() {
    install(OpenTelemetry) {
        setVerbose(true)
        addLangfuseExporter()
    }
}

fun FeatureContext.addWeaveExporter() {
    install(OpenTelemetry) {
        setVerbose(true)
        addWeaveExporter()
    }
}
