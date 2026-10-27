import ModuleDependency.getDynamicFeatureModules
import ModuleDependency.getDynamicFeatureModulesNames
import com.android.build.api.dsl.ApplicationDefaultConfig
import java.util.Properties

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

    // Upload key from keystore.properties (not in git); without the file release stays unsigned
    val keystoreFile = rootProject.file("keystore.properties")
    val uploadKey = if (keystoreFile.isFile) {
        val keystore = Properties().apply { keystoreFile.inputStream().use(::load) }
        signingConfigs.create("upload") {
            storeFile = file(keystore.getProperty("storeFile"))
            storePassword = keystore.getProperty("storePassword")
            keyAlias = keystore.getProperty("keyAlias")
            keyPassword = keystore.getProperty("keyPassword")
        }
    } else {
        null
    }

    buildTypes {
        getByName("release") {
            signingConfig = uploadKey
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

