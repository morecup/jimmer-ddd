import org.gradle.kotlin.dsl.implementation

plugins {
    id("kotlin-jvm")
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
}


dependencies {

    implementation("org.aspectj:aspectjrt:1.9.7")
    compileOnly(project(":better-ddd-core"))

    compileOnly("org.slf4j:slf4j-api:2.0.7")

}

tasks.test {
    useJUnitPlatform()
}