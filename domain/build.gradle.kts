plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.javax.inject)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
