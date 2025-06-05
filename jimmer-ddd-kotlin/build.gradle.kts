plugins {
    id("kotlin-jvm")
}


dependencies {
    api(project(":jimmer-ddd-core"))
    compileOnly(libs.jimmer.sql.kotlin)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}