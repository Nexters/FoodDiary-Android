enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FoodDiary"

// App
include(":app")

// Core modules
include(":core:common")
include(":core:ui")
include(":core:classification")

// Layer modules
include(":domain")
include(":data")

// Presentation modules
include(":presentation:home")
include(":presentation:widget")
include(":presentation:auth")
include(":presentation:image")
include(":presentation:splash")
include(":presentation:detail")
include(":presentation:modify")
include(":presentation:onboarding")
include(":presentation:mypage")
include(":presentation:webview")
include(":presentation:search")
include(":presentation:insight")
