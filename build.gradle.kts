import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.holmes"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
    application
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:1.25")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation(kotlin("serialization", version = "1.4.32"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

    testImplementation("org.assertj:assertj-core:3.12.2")
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}