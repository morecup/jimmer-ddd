plugins {
    `java-jvm`
}

dependencies {
    compileOnly(libs.jimmer.spring.boot.starter)
    implementation(project(":jimmer-ddd-java"))
}

tasks.test {
    useJUnitPlatform()
}