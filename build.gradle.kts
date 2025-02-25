plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("checkstyle")
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven {
        setUrl("https://packages.atlassian.com/maven/repository/public")
    }
}

dependencies {
    /**
    * Spring boot starters
    */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    /**
    * Database
    */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:4.3.2")
    runtimeOnly("org.postgresql:postgresql")

    /**
    * Amazon S3
    */
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.481")
    implementation("io.minio:minio:8.5.17")
    implementation("org.imgscalr:imgscalr-lib:4.2")

    /**
    * Utils & Logging
    */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")


     * Jira Impl
     */
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.1.0")
    implementation("io.atlassian.fugue:fugue:4.7.2")
    
     * API
     */
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")

    /**
    * Test containers
    */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")
    testImplementation("org.testcontainers:minio:1.20.4")

    /**
    * Tests
    */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    /**
     *  Swagger UI & OpenAI
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf("faang.school.prjectservice.service")

            limit {
                minimum = BigDecimal.valueOf(0.7)
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("service.jar")
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    checkstyle.enableExternalDtdLoad.set(true)
}

tasks.checkstyleMain {
    source = fileTree("${project.rootDir}/src/main/java")
    include("/*.java")
    exclude("/resources/")

    classpath = files()
}

tasks.checkstyleTest {
    source = fileTree("${project.rootDir}/src/test")
    include("/*.java")

    classpath = files()
}