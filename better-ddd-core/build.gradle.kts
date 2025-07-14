import org.gradle.kotlin.dsl.implementation

plugins {
    id("kotlin-jvm")
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
    kotlin("plugin.noarg") version "1.8.0"
}


dependencies {
    implementation("org.aspectj:aspectjrt:1.9.7")
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-tree:9.8")
    testAspect(project(":better-ddd-aspect"))

    compileOnly("org.slf4j:slf4j-api:2.0.7")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

noArg {
    annotation("org.morecup.jimmerddd.betterddd.annotation.AggregateRoot")
}