import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("com.diffplug.spotless") version "6.22.0"
    id("jacoco")
    kotlin("plugin.jpa") version "1.9.10"
}

group = "com.github.clubmanager1999"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.keycloak:keycloak-admin-client:22.0.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("io.rest-assured:rest-assured:5.3.2")

    testImplementation("org.testcontainers:junit-jupiter:1.19.1")
    testImplementation("org.testcontainers:postgresql:1.19.1")
    testImplementation("com.github.dasniko:testcontainers-keycloak:3.0.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktlint()
        licenseHeaderFile("${project.rootProject.projectDir}/HEADER")
    }
    kotlinGradle { ktlint() }
    flexmark {
        target("**/*.md")
        flexmark()
    }
    json {
        target("**/*.json")
        jackson()
    }
    yaml {
        target("**/*.yaml")
        jackson()
    }
}

jacoco { toolVersion = "0.8.11" }

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
