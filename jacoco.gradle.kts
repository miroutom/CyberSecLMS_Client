apply(plugin = "jacoco")

configure<JacocoPluginExtension> {
    version = "0.8.13"
}

val MINIMUM_RATIO_OF_REQUIRED_COVERAGE = "0.50"

project.afterEvaluate {
    val testTasks = tasks.matching {
        it.name.startsWith("test") && it.name.endsWith("UnitTest")
    }.toList()

    testTasks.forEach { testTask ->
        val variant = testTask.name
            .removePrefix("test")
            .removeSuffix("UnitTest")
            .replaceFirstChar { it.lowercase() }
        val variantCap = variant.replaceFirstChar { it.uppercase() }

        val reportTaskName = "jacoco${variantCap}TestReport"
        val verificationTaskName = "jacoco${variantCap}CoverageVerification"

        val buildDirVariant = "tmp/kotlin-classes/$variant/yandex"
        val execDataPath = "outputs/unit_test_code_coverage/${variant}UnitTest/test${variantCap}UnitTest.exec"

        val fileFilter = listOf(
            "**/R.class",
            "**/R\$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*"
        )

        tasks.register<JacocoReport>(reportTaskName) {
            group = "Reporting"
            dependsOn(testTask)

            reports {
                html.required.set(true)
                html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/$variant"))
                xml.required.set(true)
            }

            sourceDirectories.setFrom(
                files("$projectDir/src/main/java", "$projectDir/src/main/kotlin")
            )
            classDirectories.setFrom(
                fileTree("$buildDir/$buildDirVariant") {
                    exclude(fileFilter)
                }
            )
            executionData.setFrom(
                fileTree(layout.buildDirectory) {
                    include(
                        execDataPath,
                        "jacoco/test${variantCap}UnitTest.exec"
                    )
                }
            )
        }

        tasks.register<JacocoCoverageVerification>(verificationTaskName) {
            group = "Verification"
            dependsOn(testTask)

            violationRules {
                rule {
                    isEnabled = true
                    element = "BUNDLE"
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = BigDecimal(MINIMUM_RATIO_OF_REQUIRED_COVERAGE)
                    }
                }
            }

            sourceDirectories.setFrom(
                files("$projectDir/src/main/java", "$projectDir/src/main/kotlin")
            )
            classDirectories.setFrom(
                fileTree("$buildDir/$buildDirVariant") {
                    exclude(fileFilter)
                }
            )
            executionData.setFrom(
                fileTree(layout.buildDirectory) {
                    include(
                        execDataPath,
                        "jacoco/test${variantCap}UnitTest.exec"
                    )
                }
            )
        }
    }
}
