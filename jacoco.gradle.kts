apply(plugin = "jacoco")

configure<JacocoPluginExtension> {
    toolVersion = "0.8.13"
}

afterEvaluate {
    tasks.matching { it.name.startsWith("test") && it.name.endsWith("UnitTest") }
        .forEach { testTask ->

            val variant = testTask.name.removePrefix("test").removeSuffix("UnitTest")
                .replaceFirstChar { it.lowercase() }
            val variantCap = variant.replaceFirstChar { it.uppercaseChar() }

            val reportTaskName = "jacoco${variantCap}TestReport"
            val verificationTaskName = "jacoco${variantCap}CoverageVerification"

            val buildDirVariant = "tmp/kotlin-classes/$variant/hse"
            val execDataPath =
                "outputs/unit_test_code_coverage/${variant}UnitTest/test${variantCap}UnitTest.exec"

            val fileFilter = listOf(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*Test*.*",
                "android/**/*.*",
                "**/*Composable*.*",
                "**/*Preview*.*"
            )

            tasks.register<JacocoReport>(reportTaskName) {
                group = "Reporting"
                dependsOn(testTask)

                reports {
                    html.required.set(true)
                    html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/$variant"))
                    xml.required.set(false)
                }

                sourceDirectories.from(
                    files("$projectDir/src/main/java", "$projectDir/src/main/kotlin")
                )
                classDirectories.setFrom(
                    fileTree("build/${buildDirVariant}") {
                        exclude(fileFilter)
                    }
                )
                executionData.from(
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
                        enabled = true
                        element = "BUNDLE"
                        limit {
                            counter = "BRANCH"
                            value = "COVEREDRATIO"
                            minimum = "0.50".toBigDecimal()
                        }
                    }
                }

                sourceDirectories.from(
                    files("$projectDir/src/main/java", "$projectDir/src/main/kotlin")
                )
                classDirectories.setFrom(
                    fileTree("build/${buildDirVariant}") {
                        exclude(fileFilter)
                    }
                )
                executionData.from(
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
