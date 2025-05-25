plugins {
    id("buildsrc.convention.kotlin-jvm")
}

val jimmerVersion: String = "0.2.0-0.9.81"

dependencies {
    implementation(project(":jimmer-ddd-core"))
    implementation("io.github.morecup:jimmer-spring-boot-starter:${jimmerVersion}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}