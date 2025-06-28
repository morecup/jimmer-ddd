import org.gradle.kotlin.dsl.implementation

plugins {
    id("kotlin-jvm")
}


dependencies {

    implementation("org.aspectj:aspectjrt:1.9.7")
    implementation(project(":better-ddd-core"))

    compileOnly("org.slf4j:slf4j-api:2.0.7")

}

tasks.test {
    useJUnitPlatform()
}