plugins {
    `kotlin-dsl`
}

group = "com.dsbezerra.shootsheet.buildlogic"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dsbezerra.shootsheet.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "dsbezerra.shootsheet.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "dsbezerra.shootsheet.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "dsbezerra.shootsheet.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("spotless") {
            id = "dsbezerra.shootsheet.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
    }
}