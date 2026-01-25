plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Testing
    testImplementation(libs.junit)
}
