plugins {
    `java-jvm`
}

dependencies {
    compileOnly(libs.jimmer.spring.boot.starter)
    api(project(":jimmer-ddd-java"))
    implementation("org.springframework.boot:spring-boot-starter-aop:2.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("cn.hutool:hutool-all:5.8.22")
    testImplementation(libs.jimmer.spring.boot.starter)
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.0")
    testRuntimeOnly("mysql:mysql-connector-java:8.0.30")
    testAnnotationProcessor(libs.jimmer.apt)
    implementation("org.springframework.boot:spring-boot-starter-aop")
}

tasks.test {
    useJUnitPlatform()
}