plugins {
    `java-jvm`
}

dependencies {
    implementation(project(":jimmer-ddd-core"))
}

tasks.test {
    useJUnitPlatform()
}