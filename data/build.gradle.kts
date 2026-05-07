import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

fun localOrEnv(localKey: String, envKey: String): String {
    val localValue = localProperties.getProperty(localKey, "").trim()
    return localValue.ifEmpty { System.getenv(envKey).orEmpty().trim() }
}

android {
    namespace = "com.nexters.fooddiary.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiBaseUrl = localOrEnv("api.base.url", "API_BASE_URL")
            .ifEmpty { "https://api.example.com/" }

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"$apiBaseUrl\""
        )
    }

    buildTypes {
        release {
            val apiBaseUrl = localOrEnv("api.base.url", "API_BASE_URL")

            if (apiBaseUrl.isNotBlank()) {
                buildConfigField(
                    "String",
                    "API_BASE_URL",
                    "\"$apiBaseUrl\""
                )
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// Resolve duplicate META-INF resources for androidTest (e.g. jspecify + logging-interceptor)
android.packaging {
    resources {
        excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
}

// CI 및 릴리즈 빌드에서 API_BASE_URL 유효성 체크
afterEvaluate {
    tasks.matching {
        it.name.contains("assemble", ignoreCase = true) || it.name.contains("bundle", ignoreCase = true)
    }.configureEach {
        doFirst {
            val apiBaseUrl = localOrEnv("api.base.url", "API_BASE_URL")
            val isCi = System.getenv("CI").orEmpty().equals("true", ignoreCase = true)

            if (isCi && apiBaseUrl.isBlank()) {
                throw GradleException(
                    "API_BASE_URL must be set in CI environment"
                )
            }
        }
    }

    tasks.matching { 
        it.name.contains("Release", ignoreCase = true) && 
        (it.name.contains("assemble", ignoreCase = true) || it.name.contains("bundle", ignoreCase = true))
    }.configureEach {
        doFirst {
            val apiBaseUrl = localOrEnv("api.base.url", "API_BASE_URL")

            if (apiBaseUrl.isBlank()) {
                throw GradleException(
                    "api.base.url in local.properties or API_BASE_URL env must be set for release builds"
                )
            }
        }
    }
}

dependencies {
    // Modules
    implementation(projects.core.common)
    implementation(projects.core.classification)
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase Auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.installations)
    implementation(libs.firebase.messaging)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)

    // Instrumentation testing (androidTest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.espresso.core)
}
