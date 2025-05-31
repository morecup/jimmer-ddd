plugins {
    `java-jvm`
}

dependencies {
    compileOnly(libs.jimmer.spring.boot.starter)
    api(project(":jimmer-ddd-java"))
}

tasks.test {
    useJUnitPlatform()
}