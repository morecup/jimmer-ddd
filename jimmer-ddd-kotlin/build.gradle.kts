plugins {
    id("buildsrc.convention.kotlin-jvm")
}


dependencies {
    implementation(project(":jimmer-ddd-core"))
    compileOnly(libs.jimmer.sql)
    compileOnly(libs.jimmer.spring.boot.starter)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}