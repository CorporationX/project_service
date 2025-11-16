plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.2.40"
    jacoco
    checkstyle
}

group = "faang.school"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://packages.atlassian.com/maven/repository/public")
}

dependencies {
    /** ------------------------------
     * Spring Boot Starters
     * ------------------------------ */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    /** ------------------------------
     * Database
     * ------------------------------ */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:4.3.2")
    runtimeOnly("org.postgresql:postgresql")

    /** ------------------------------
     * Amazon S3
     * ------------------------------ */
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.481")

    /** ------------------------------
     *  Swagger / OpenAPI
     * ------------------------------ */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    /** ------------------------------
     * Utils & Logging
     * ------------------------------ */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")

    /** ------------------------------
     * Lombok & MapStruct
     * ------------------------------ */
    val lombokVersion = "1.18.34"
    val mapstructVersion = "1.5.3.Final"

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    /** ------------------------------
     * Jira Integration
     * ------------------------------ */
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64")
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.2.7")
    implementation("com.atlassian.jira:jira-rest-java-client-api:5.2.7")
    implementation("io.atlassian.fugue:fugue:4.7.2")

    /** ------------------------------
     *  Testcontainers
     * ------------------------------ */
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

    /** ------------------------------
     * Unit Tests
     * ------------------------------ */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.40".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            excludes = listOf(
                "faang.school.projectservice.ProjectServiceApplication",
                "faang.school.projectservice.dto.*",
                "faang.school.projectservice.model.*",
                "faang.school.projectservice.config.*",
                "faang.school.projectservice.integration.jira.config.*",
                "faang.school.projectservice.integration.jira.event.*",
                "faang.school.projectservice.integration.jira.exception.*",
                "faang.school.projectservice.client.*",
                "faang.school.projectservice.integration.jira.scheduled.*",
                "faang.school.projectservice.integration.jira.service.JiraOAuthService",
                "faang.school.projectservice.integration.jira.service.JiraTokenService",
                "faang.school.projectservice.integration.jira.client.JiraOAuthClient",
                "faang.school.projectservice.integration.jira.client.OAuthTokenRefreshInterceptor",
                "faang.school.projectservice.integration.jira.controller.JiraOAuthController",
                "faang.school.projectservice.integration.jira.JiraSystemClient",
                "faang.school.projectservice.integration.jira.oauth.OAuthStateManager",
                "faang.school.projectservice.integration.jira.oauth.JiraOAuthTokenManager",
                "faang.school.projectservice.integration.jira.cache.*",
                "faang.school.projectservice.integration.jira.metrics.*",
                "faang.school.projectservice.integration.jira.websocket.*",
                "faang.school.projectservice.integration.jira.controller.JiraCacheController",
                "faang.school.projectservice.integration.jira.controller.JiraSyncController"
            )
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.30".toBigDecimal()
            }
        }
    }
}

tasks.bootJar {
    archiveFileName.set("service.jar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    // Ensure annotation processing is enabled
    options.annotationProcessorPath = configurations.getByName("annotationProcessor")
    options.isIncremental = true
}
checkstyle {
    toolVersion = "10.17.0"
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    checkstyle.enableExternalDtdLoad.set(true)
}

tasks.checkstyleMain {
    source = fileTree("${project.rootDir}/src/main/java")
    include("**/*.java")
    exclude("**/resources/**")

    classpath = files()
}

tasks.checkstyleTest {
    source = fileTree("${project.rootDir}/src/test")
    include("**/*.java")

    classpath = files()
}
