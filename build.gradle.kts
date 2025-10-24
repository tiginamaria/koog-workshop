plugins {
    kotlin("jvm") version "2.2.20"
}

group = "ai.koog"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.koog.agents)
    implementation(libs.logback.classic)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("run01") {
    group = "application"
    description = "Run 01-PromptTask.kt"
    mainClass.set("ai.koog.workshop.intro._01_PromptTaskKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("run02") {
    group = "application"
    description = "Run 02-PromptExecutorTask.kt"
    mainClass.set("ai.koog.workshop.intro._02_PromptExecutorTaskKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("run03") {
    group = "application"
    description = "Run 03-ToolTask.kt"
    mainClass.set("ai.koog.workshop.intro._03_ToolTaskKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("run04") {
    group = "application"
    description = "Run 04-ToolSetTask.kt"
    mainClass.set("ai.koog.workshop.intro._04_ToolSetTaskKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("run05") {
    group = "application"
    description = "Run 05-ToolRegistryTask.kt"
    mainClass.set("ai.koog.workshop.intro._05_ToolRegistryTaskKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("run06") {
    group = "application"
    description = "Run 06-SimpleAgent.kt"
    mainClass.set("ai.koog.workshop.intro._06_SimpleAgentKt")
    classpath = sourceSets["main"].runtimeClasspath
}