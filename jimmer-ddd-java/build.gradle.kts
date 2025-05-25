plugins {
    `java-jvm`
}

dependencies {
    compileOnly(libs.jimmer.sql)
    implementation(project(":jimmer-ddd-core"))
}

tasks.test {
    useJUnitPlatform()
}