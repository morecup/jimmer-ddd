plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

val jimmerVersion: String = "0.2.0-0.9.81"

dependencies {
    compileOnly("io.github.morecup:jimmer-sql-kotlin:${jimmerVersion}")
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-tree:9.8")
    compileOnly("org.slf4j:slf4j-api:2.0.7")
    testImplementation(kotlin("test"))
    testImplementation("cn.hutool:hutool-all:5.8.22")
    testImplementation("io.github.morecup:jimmer-spring-boot-starter:${jimmerVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.0")
    testRuntimeOnly("mysql:mysql-connector-java:8.0.30")
    kspTest("io.github.morecup:jimmer-ksp:${jimmerVersion}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}