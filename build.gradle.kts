plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.openapi.generator") version "7.4.0"
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
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

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

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

    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.5.2.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")


    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    /**
     * OpenAPI dependencies
     */
    implementation("org.openapitools:jackson-databind-nullable:0.2.4")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/src/main/resources/static/internship-api.yaml")
    outputDir.set("$buildDir/generated-sources/openapi")

    apiPackage.set("faang.school.projectservice.api")
    modelPackage.set("faang.school.projectservice.apimodel")

    generateModelTests.set(false)
    generateApiTests.set(false)
    skipValidateSpec.set(true)

    additionalProperties.set(
        mapOf(
            "delegatePattern" to "true",
            "performBeanValidation" to "false",
            "useBeanValidation" to "false",
            "useJakartaEe" to "true"
        )
    )
}

sourceSets["main"].java.srcDir("$buildDir/generated-sources/openapi/src/main/java")

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}