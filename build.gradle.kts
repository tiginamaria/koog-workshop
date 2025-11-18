plugins {
    kotlin("jvm") version "2.2.20"
}

group = "ai.koog"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
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