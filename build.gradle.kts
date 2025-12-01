plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.pragma"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra.apply {
    set("springdocVersion", "3.0.0")
    set("mapstructVersion", "1.6.2")
    set("jjwtVersion", "0.12.5")
    set("postgresVersion", "42.7.3")
    set("lombokVersion", "1.18.34")
    set("lombokMapstructBindingVersion", "0.2.0")
    set("openfeignVersion", "5.0.0")
}

dependencies {
    // Compilation and Runtime
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdocVersion")}")

    // OpenFeign for microservices communication
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:${property("openfeignVersion")}")

    // New: Add actuator to satisfy lifecycle auto-configuration dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // MapStruct
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")

    // Runtime Only
    runtimeOnly("org.postgresql:postgresql:${property("postgresVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jjwtVersion")}")

    // Compile Only
    compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")

    // Annotation Processors
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${property("lombokMapstructBindingVersion")}")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
