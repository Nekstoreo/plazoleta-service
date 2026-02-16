plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

val jacocoExcludes = listOf(
    "**/infrastructure/configuration/**",
    "**/infrastructure/initialization/**",
    "**/*Configuration.class",
    "**/*Config.class",
    "**/*Application.class",
    "**/*Dto.class",
    "**/*Request.class",
    "**/*Response.class"
)

group = "com.pragma"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
    set("lombokVersion", "1.18.42")
    set("lombokMapstructBindingVersion", "0.2.0")
    set("flywayVersion", "10.15.0")
    set("openfeignVersion", "5.0.0")
}

dependencies {
    // Implementation
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdocVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:${property("openfeignVersion")}")

    // Compile Only
    compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")

    // Annotation Processors
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${property("lombokMapstructBindingVersion")}")

    // Runtime Only
    runtimeOnly("org.postgresql:postgresql:${property("postgresVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jjwtVersion")}")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map { directory ->
            fileTree(directory) {
                exclude(jacocoExcludes)
            }
        })
    )
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map { directory ->
            fileTree(directory) {
                exclude(jacocoExcludes)
            }
        })
    )
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf("com/pragma/*/domain/usecase*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.95".toBigDecimal()
            }
        }
        rule {
            element = "PACKAGE"
            includes = listOf("com/pragma/*/domain/model*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
        }
        rule {
            element = "PACKAGE"
            includes = listOf("com/pragma/*/application*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.85".toBigDecimal()
            }
        }
        rule {
            element = "PACKAGE"
            includes = listOf("com/pragma/*/infrastructure/input*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
        rule {
            element = "PACKAGE"
            includes = listOf("com/pragma/*/infrastructure/output*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
