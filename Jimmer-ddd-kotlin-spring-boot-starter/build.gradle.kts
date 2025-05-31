plugins {
    id("buildsrc.convention.kotlin-jvm")
}


dependencies {
    api(project(":jimmer-ddd-kotlin"))
    compileOnly(libs.jimmer.spring.boot.starter)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}