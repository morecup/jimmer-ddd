plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(project(":jimmer-ddd-core"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}