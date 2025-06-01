plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}


dependencies {
    api(project(":jimmer-ddd-kotlin"))
    compileOnly(libs.jimmer.spring.boot.starter)
    testImplementation(kotlin("test"))
    testImplementation("cn.hutool:hutool-all:5.8.22")
    testImplementation(libs.jimmer.spring.boot.starter)
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.0")
    testRuntimeOnly("mysql:mysql-connector-java:8.0.30")
    kspTest(libs.jimmer.ksp)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}