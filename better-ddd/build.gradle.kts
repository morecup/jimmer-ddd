import org.gradle.kotlin.dsl.implementation

plugins {
    id("kotlin-jvm")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.22" // 使用与Kotlin版本匹配的插件版本
//    id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.14.18"
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
    kotlin("plugin.noarg") version "1.8.0"
}


dependencies {
    implementation(libs.jimmer.sql)
    implementation("org.ow2.asm:asm:9.8")

    implementation("org.aspectj:aspectjrt:1.9.7")
//    aspect("org.aspectj:aspectjweaver:1.9.7")
    testAspect(project(":better-ddd"))

    implementation("org.ow2.asm:asm-tree:9.8")
    implementation("net.bytebuddy:byte-buddy:1.14.18")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.18")
    compileOnly("org.slf4j:slf4j-api:2.0.7")
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


allOpen {
    annotation("org.morecup.jimmerddd.betterddd.annotation.AggregateRoot")
}

noArg {
    annotation("org.morecup.jimmerddd.betterddd.annotation.AggregateRoot")
}