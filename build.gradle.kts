import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.gms.services) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.sentry.android.gradle) apply false
}

val requiredProperties = mapOf(
    "api.base.url" to "API_BASE_URL",
    "web.client.id" to "WEB_CLIENT_ID",
)

val validateRequiredProperties = tasks.register("validateRequiredProperties") {
    group = "verification"
    description = "Validates required local or CI configuration values."

    doLast {
        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use(::load)
            }
        }
        val missingKeys = requiredProperties.filter { (propertyKey, environmentKey) ->
            val localValue = localProperties.getProperty(propertyKey)?.trim()
            val environmentValue = providers.environmentVariable(environmentKey)
                .orNull
                ?.trim()

            localValue.isNullOrEmpty() && environmentValue.isNullOrEmpty()
        }.keys

        if (missingKeys.isNotEmpty()) {
            throw GradleException(
                "Required properties are missing or blank: ${missingKeys.joinToString()}"
            )
        }

        logger.lifecycle("Required properties are configured.")
    }
}

subprojects {
    tasks.matching { it.name == "preBuild" }.configureEach {
        dependsOn(validateRequiredProperties)
    }

    pluginManager.withPlugin("com.android.application") {
        extensions.configure<ApplicationExtension> {
            lint {
                disable += "NullSafeMutableLiveData"
            }
        }
    }

    pluginManager.withPlugin("com.android.library") {
        extensions.configure<LibraryExtension> {
            lint {
                disable += "NullSafeMutableLiveData"
            }
        }
    }
}
