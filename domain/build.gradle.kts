plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Javax Inject (for @Inject annotation without Hilt dependency)
    implementation(libs.javax.inject)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
