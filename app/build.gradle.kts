import ModuleDependency.getDynamicFeatureModules
import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    androidApp()
    kotlinAndroid()
    kotlinKapt()
}

android {

    defaultConfig {
        applicationId = Apps.applicationId
        versionCode = Apps.versionCode
        versionName = Apps.versionName

        buildConfigField("modules", getDynamicFeatureModules().toSet())
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro", "proguard-rules-dynamic-features.pro")
        }
    }

    // Each feature module that is included in settings.gradle.kts is added here as dynamic feature
    setDynamicFeatures(getDynamicFeatureModules().toMutableSet())
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

    // Libraries which can be re-used in other modules should use the `api` keyword.
    // This way they can be shared with dependent feature modules.
    api(Lib.playCore)
    api(Lib.playCoreKtx)
}

fun ApplicationDefaultConfig.buildConfigField(name: String, value: Set<String>) {
    // Generates String that holds Java String Array code
    val strValue = value.joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
    buildConfigField("String[]", name, strValue)
}

