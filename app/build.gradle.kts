import ModuleDependency.getDynamicFeatureModules
import ModuleDependency.getDynamicFeatureModulesNames
import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    id("convention.android-application")
    id("convention.compose")
}

android {
    namespace = applicationID
    defaultConfig {
        applicationId = applicationID
        versionCode = getVersionCode()
        versionName = getVersionName()

        buildConfigField("modules", getDynamicFeatureModulesNames())
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            // R8 matters for Compose: without it release frames are noticeably slower
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // Each feature module that is included in settings.gradle.kts is added here as dynamic feature
    dynamicFeatures += getDynamicFeatureModules()
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.coroutines)
    implementation(libs.exoPlayer)
    implementation(libs.splashScreen)

    api(libs.playCoreKtx)
    api(libs.playFeatureKtx)
}

fun ApplicationDefaultConfig.buildConfigField(name: String, value: Set<String>) {
    // Generates String that holds Java String Array code
    val strValue = value.joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
    buildConfigField("String[]", name, strValue)
}

