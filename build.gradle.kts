plugins {
    java
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("jacoco")
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven {
        name = "atlassian-public"
        url = uri("https://packages.atlassian.com/maven/repository/public")
    }
}

dependencies {
    /**
     * Google calendar API
     */
    implementation("com.google.auth:google-auth-library-oauth2-http:1.31.0")
    implementation("com.google.api-client:google-api-client:2.7.2")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20250115-2.0.0")
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("io.vertx:vertx-web-templ-freemarker:4.4.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.xhtmlrenderer:flying-saucer-pdf-openpdf:9.1.22")
    implementation("com.github.librepdf:openpdf:1.3.30")

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
    implementation("org.apache.pdfbox:pdfbox:2.0.27")
    implementation("io.minio:minio:8.3.4")

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

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    /**
     * Jira
     */
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.2.4") {
        exclude(group = "org.glassfish.jersey.core", module = "jersey-common")
    }
    implementation("com.atlassian.jira:jira-rest-java-client-api:6.0.1")
    implementation("org.glassfish.jersey.core:jersey-common:2.27")
    implementation("io.atlassian.fugue:fugue:5.0.2")
    /**
     * ImageResizer
     */
    implementation("net.coobird:thumbnailator:0.4.20")


    /**
     * Swagger
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    sourceSets {
        named("test") {
            java.srcDirs("src/test/java")
            resources.srcDirs("src/test/resources")
        }
    }
    classDirectories.setFrom(
        fileTree(project.buildDir.resolve("classes/java/main")) {
            include("**/service/**")
        }
    )

    executionData.setFrom(fileTree(project.buildDir).include("jacoco/test.exec"))
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    classDirectories.setFrom(
        fileTree(project.buildDir.resolve("classes/java/main")) {
            include("**/service/**)")
        }
    )

    tasks.processTestResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = 0.70.toBigDecimal()
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}