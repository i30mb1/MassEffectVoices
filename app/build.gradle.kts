import ModuleDependency.getDynamicFeatureModules
import ModuleDependency.getDynamicFeatureModulesNames
import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    id("convention.android-application")
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
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro", "proguard-rules-dynamic-features.pro")
        }
    }

    // Each feature module that is included in settings.gradle.kts is added here as dynamic feature
    setDynamicFeatures(getDynamicFeatureModules())
}

dependencies {
    implementation(libs.material)
    implementation(libs.constraintLayout)
    implementation(libs.coreKtx)
    implementation(libs.coil)
    implementation(libs.coroutines)
    implementation(libs.fragmentKtx)
    implementation(libs.activityKtx)
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

