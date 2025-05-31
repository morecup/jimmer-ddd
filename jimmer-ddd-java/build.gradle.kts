plugins {
    `java-jvm`
}

dependencies {
    compileOnly(libs.jimmer.sql)
    api(project(":jimmer-ddd-core"))
}

tasks.test {
    useJUnitPlatform()
}