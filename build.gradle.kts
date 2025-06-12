plugins {
    java
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.9.0"
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
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
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.464")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.0")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

    /**
     * swagger
     */
    implementation( "org.springdoc", "springdoc-openapi-starter-webmvc-ui",  "2.0.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    checkstyle.enableExternalDtdLoad.set(true)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.build {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestReport {
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).apply {
            exclude(
//                "**/mapper/**",
//                "**/entity/**",
//                "**/client/**",
//                "**/config/**",
//                "**/dto/**",
//                "**/model/**",
//                "**/controller/**",
//                "**/repository/**",
//                "**/**Test.class",
//                "**/ProjectServiceApplication.class",
//                "**/**Impl.class",
            )
        }
    }))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
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

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}

jacoco {
    toolVersion = "0.8.13"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    reports {
        dependsOn(tasks.test) // tests are required to run before generating the report
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.8.toBigDecimal()
            }
        }

        rule {
            enabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = 0.3.toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport {
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).apply {
            exclude(
                "**/mapper/**",                         //Исключить mapper
                "**/entity/**",                         //Исключить пакет с сущностями
                "**/client/**",                         //Исключить пакет client
                "**/config/**",                         //Исключить пакет config
                "**/dto/**",                            //Исключить пакет с dto
                "**/model/**",                          //Исключить пакет model
                "**/controller/**",                     //Исключить контроллеры
                "**/repository/**",                     //Исключить репозитории
                "**/**Test.class",                      //Исключить тесты
                "**/ProjectServiceApplication.class",   //Исключить класс с main
                "**/**Impl.class",                      //Исключить Impl классы
            )
        }
    }))

}