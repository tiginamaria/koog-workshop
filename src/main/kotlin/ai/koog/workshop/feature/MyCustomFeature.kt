package ai.koog.workshop.feature

import ai.koog.agents.core.agent.entity.AIAgentStorageKey
import ai.koog.agents.core.feature.AIAgentGraphFeature
import ai.koog.agents.core.feature.config.FeatureConfig
import ai.koog.agents.core.feature.pipeline.AIAgentGraphPipeline

class MyCustomFeature {

    class MyCustomFeatureConfig : FeatureConfig() {
        var myCustomConfigField: String = ""
    }

    companion object Feature : AIAgentGraphFeature<MyCustomFeatureConfig, MyCustomFeature> {
        override val key: AIAgentStorageKey<MyCustomFeature> = AIAgentStorageKey("my-custom-feature")

        override fun createInitialConfig(): MyCustomFeatureConfig {
            return MyCustomFeatureConfig()
        }

        override fun install(
            config: MyCustomFeatureConfig,
            pipeline: AIAgentGraphPipeline
        ): MyCustomFeature {

            val feature = MyCustomFeature()

            pipeline.interceptAgentStarting(this) { event ->
                // TODO: Do some stuff
            }

            pipeline.interceptToolCallStarting(this) { event ->
                // TODO: Do some stuff
            }

            return feature
        }
    }

}
