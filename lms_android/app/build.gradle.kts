import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    id("com.google.devtools.ksp")
    alias(libs.plugins.screenshot)
}

apply(from = "../../jacoco.gradle.kts")

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val baseUrl: String =
    System.getProperty("BASE_URL")
        ?: System.getenv("BASE_URL")
        ?: localProperties.getProperty("BASE_URL")
        ?: "\"https://default-url.example.com/api/\""

val uploadsUrl: String =
    System.getProperty("UPLOADS_URL")
        ?: System.getenv("UPLOADS_URL")
        ?: localProperties.getProperty("UPLOADS_URL")
        ?: "\"https://default-url.example.com/api/\""

android {
    namespace = "hse.diploma.cybersecplatform"
    compileSdk = 35
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "hse.diploma.cybersecplatform"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile =
                file("$rootDir/cybersec-release.jks")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = "cybersec"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", baseUrl)
            buildConfigField("String", "UPLOADS_URL", uploadsUrl)
        }

        debug {
            buildConfigField("String", "BASE_URL", baseUrl)
            buildConfigField("String", "UPLOADS_URL", uploadsUrl)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes +=
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                    "META-INF/NOTICE.md",
                )
        }
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    testOptions {
        screenshotTests {
            imageDifferenceThreshold = 0.1f // 10%
        }
    }
}

dependencies {
    // --- Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.multidex)

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.placeholder)

    // --- Kotlin ---
    implementation(platform(libs.kotlin.bom))

    // --- Images ---
    implementation(libs.coil.kt.coil.compose)

    // --- DI ---
    implementation(libs.dagger)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment)
    ksp(libs.dagger.compiler)

    // --- REST API ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)

    // --- Testing ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.espresso.idling.resource)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    screenshotTestImplementation(libs.androidx.compose.ui.tooling)

    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
}

tasks.register("pullRequestCheck") {
    group = "verification"
    description = "Run lint and ktlint checks for pull request."
    dependsOn("lint", "ktlintCheck")
}
