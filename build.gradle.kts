plugins {
    id("java")
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "hu.davidder"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val vertxVersion = "4.5.12"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
	implementation("com.auth0:java-jwt:4.5.0")
    implementation("io.vertx:vertx-core")
    implementation("io.vertx:vertx-auth-oauth2")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.4.2")
	implementation("org.springframework.security:spring-security-oauth2-resource-server:6.4.3")
	implementation("org.springframework.security:spring-security-oauth2-jose:6.4.3")
	implementation("org.springframework.security:spring-security-oauth2-client:6.4.3")
	implementation("org.keycloak:keycloak-spring-boot-starter:25.0.3")
	implementation("org.keycloak:keycloak-spring-security-adapter:25.0.3")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.4.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web-client")
    implementation("io.vertx:vertx-config")
    implementation("io.vertx:vertx-web-graphql")
    implementation("io.vertx:vertx-health-check")
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-json-schema")
    implementation("io.vertx:vertx-web-api-contract")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.vertx:vertx-rx-java3:4.5.12")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

