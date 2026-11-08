import ModuleDependency.getDynamicFeatureModules
import ModuleDependency.getDynamicFeatureModulesNames
import com.android.build.api.dsl.ApplicationDefaultConfig
import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import java.util.Properties

plugins {
    id("convention.android-application")
    id("convention.compose")
    alias(libs.plugins.playPublisher)
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
            // R8 code and resource shrinking. Matters for Compose: without it release frames are noticeably slower.
            // Resources looked up by name are kept in res/raw/keep.xml
            optimization {
                enable = true
                keepRules {
                    files.add(file("proguard-rules.pro"))
                }
            }
        }
    }

    // Each feature module that is included in settings.gradle.kts is added here as dynamic feature
    dynamicFeatures += getDynamicFeatureModules()
}

// ./gradlew :app:publishReleaseBundle uploads the signed bundle to production (key file is not in git)
play {
    serviceAccountCredentials.set(rootProject.file("play-service-account.json"))
    defaultToAppBundles.set(true)
    track.set("production")
    releaseStatus.set(ReleaseStatus.COMPLETED)
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.coroutines)
    implementation(libs.exoPlayer)
    implementation(libs.splashScreen)
    implementation(libs.billing)

    api(libs.playFeatureKtx)

    constraints {
        // Play libraries still pull androidx.fragment 1.1.0, which Play Console reports as outdated
        implementation(libs.fragment)
    }
}

fun ApplicationDefaultConfig.buildConfigField(name: String, value: Set<String>) {
    // Generates String that holds Java String Array code
    val strValue = value.joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
    buildConfigField("String[]", name, strValue)
}

