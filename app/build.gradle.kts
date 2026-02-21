import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.sentry.android.gradle)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun localOrEnv(localKeys: List<String>, envKey: String): String {
    val localValue = localKeys
        .asSequence()
        .map { key -> localProperties.getProperty(key, "").trim() }
        .firstOrNull { it.isNotEmpty() }
        .orEmpty()

    return localValue.ifEmpty { System.getenv(envKey).orEmpty().trim() }
}

val devStoreFile = localOrEnv(listOf("dev.store.file"), "DEV_KEYSTORE_PATH")
val devStorePassword = localOrEnv(listOf("dev.store.password"), "DEV_KEYSTORE_PASSWORD")
val devKeyAlias = localOrEnv(listOf("dev.key.alias"), "DEV_KEY_ALIAS")
val devKeyPassword = localOrEnv(listOf("dev.key.password"), "DEV_KEY_PASSWORD")

val releaseStoreFile = localOrEnv(listOf("store.file"), "RELEASE_STORE_FILE")
val releaseStorePassword = localOrEnv(listOf("store.password"), "RELEASE_STORE_PASSWORD")
val releaseKeyAlias = localOrEnv(listOf("key.alias"), "RELEASE_KEY_ALIAS")
val releaseKeyPassword = localOrEnv(listOf("key.password"), "RELEASE_KEY_PASSWORD")
val hasReleaseSigningConfig = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { it.isNotBlank() }

android {
    namespace = "com.nexters.fooddiary"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nexters.fooddiary"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = project.findProperty("versionCode")?.toString()?.toInt() ?: 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val webClientId = localProperties.getProperty("web.client.id", "")
            .ifEmpty { System.getenv("WEB_CLIENT_ID").orEmpty() }

        if (webClientId.isNotEmpty()) {
            resValue("string", "custom_web_client_id", webClientId)
        }

        val sentryDsn = localProperties.getProperty("sentry.dsn", "")
            .ifEmpty { System.getenv("SENTRY_DSN").orEmpty() }
            .trim()
        buildConfigField("String", "SENTRY_DSN", "\"$sentryDsn\"")
        manifestPlaceholders["sentryDsn"] = sentryDsn
    }

    signingConfigs {
        create("dev") {
            storeFile = rootProject.file(devStoreFile)
            storePassword = devStorePassword
            keyAlias = devKeyAlias
            keyPassword = devKeyPassword
        }
        if (hasReleaseSigningConfig) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = project.findProperty("releaseMinify") != "false"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "USE_MOCK_API", "false")
            signingConfig = signingConfigs.findByName("release")
        }
        create("debugRelease") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            signingConfig = signingConfigs.findByName("dev")
            buildConfigField("boolean", "USE_MOCK_API", "false")
        }
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("boolean", "USE_MOCK_API", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

sentry {
    org.set(localProperties.getProperty("sentry.org", ""))
    projectName.set(localProperties.getProperty("sentry.project", ""))
    authToken.set(localProperties.getProperty("sentry.auth.token", ""))
    includeSourceContext.set(true)
    autoInstallation {
        enabled.set(true)
        sentryVersion.set(libs.versions.sentryAndroid.get())
    }
}

dependencies {
    // Modules
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.classification)
    implementation(projects.domain)
    implementation(projects.data)

    implementation(projects.presentation.home)
    implementation(projects.presentation.widget)
    implementation(projects.presentation.image)
    implementation(projects.presentation.auth)
    implementation(projects.presentation.mypage)
    implementation(projects.presentation.webview)
    implementation(projects.presentation.splash)
    implementation(projects.presentation.detail)
    implementation(projects.presentation.onboarding)
    implementation(projects.presentation.insight)
    implementation(projects.presentation.modify)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.haze)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.core)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Mavericks
    implementation(libs.mavericks.compose)

    // Firebase Cloud Messaging
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.sentry.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
