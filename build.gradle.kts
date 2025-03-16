jacoco {
    toolVersion = "0.8.9"
    reportsDirectory.set(layout.buildDirectory.dir("$buildDir/reports/jacoco"))
}

val exclusions = listOf(
    "**/UserServiceApplication*",
    "**/controller/**",
    "**/mapper/**",
    "**/entity/**",
    "**/dto/**",
    "**/exception/**",
    "**/com/json/**",
    "**/client/**",
    "**/config/**"
)

tasks.test {
    finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

tasks.build {
    dependsOn(tasks.jacocoTestCoverageVerification)
    doLast {
        println("Build completed successfully with test coverage verification.")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    classDirectories.setFrom(
        files(sourceSets.main.get().output.asFileTree.matching {
            exclude(exclusions)
        })
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            limit {
                minimum = 0.7.toBigDecimal()
            }
        }
        rule {
            element = "METHOD"
            limit {
                minimum = 0.8.toBigDecimal()
            }
        }
        rule {
            element = "LINE"
            limit {
                minimum = 0.6.toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(
        files(sourceSets.main.get().output.asFileTree.matching {
            exclude(exclusions)
        })
    )
}