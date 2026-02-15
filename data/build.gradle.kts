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

android {
    namespace = "com.nexters.fooddiary.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiBaseUrl = localProperties.getProperty("api.base.url")
            ?: "https://api.example.com/"

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"$apiBaseUrl\""
        )
    }

    buildTypes {
        release {
            val apiBaseUrl = localProperties.getProperty("api.base.url")

            if (!apiBaseUrl.isNullOrBlank()) {
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

// 릴리즈 빌드가 실제로 실행될 때만 API_BASE_URL을 체크
afterEvaluate {
    tasks.matching { 
        it.name.contains("Release", ignoreCase = true) && 
        (it.name.contains("assemble", ignoreCase = true) || it.name.contains("bundle", ignoreCase = true))
    }.configureEach {
        doFirst {
            val apiBaseUrl = localProperties.getProperty("api.base.url")

            if (apiBaseUrl.isNullOrBlank()) {
                throw GradleException(
                    "api.base.url must be set in local.properties for release builds"
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

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

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

    // Google Sign-In
    implementation(libs.play.services.auth)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
