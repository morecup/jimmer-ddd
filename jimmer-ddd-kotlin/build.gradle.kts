plugins {
    id("kotlin-jvm")
}


dependencies {
    api(project(":jimmer-ddd-core"))
    compileOnly(libs.jimmer.sql)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}