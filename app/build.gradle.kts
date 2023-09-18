import ModuleDependency.getDynamicFeatureModules
import ModuleDependency.getDynamicFeatureModulesNames
import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    androidApp()
    kotlinAndroid()
}

android {

    defaultConfig {
        applicationId = Apps.applicationId
        versionCode = Apps.versionCode
        versionName = Apps.versionName

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
    implementation(Lib.material)
    implementation(Lib.constraintLayout)
    implementation(Lib.coreKtx)
    implementation(Lib.coil)
    implementation(Lib.coroutinesLifecycle)
    implementation(Lib.coroutinesLivedata)
    implementation(Lib.coroutinesViewmodel)
    implementation(Lib.fragmentKtx)
    implementation(Lib.activityKtx)
    implementation(Lib.coroutines)
    implementation(Lib.exoPlayer)
    implementation(Lib.splashScreen)

    api(Lib.playCore)
    api(Lib.playCoreKtx)
}

fun ApplicationDefaultConfig.buildConfigField(name: String, value: Set<String>) {
    // Generates String that holds Java String Array code
    val strValue = value.joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
    buildConfigField("String[]", name, strValue)
}

