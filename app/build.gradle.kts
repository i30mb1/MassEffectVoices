import ModuleDependency.getDynamicFeatureModules
import com.android.build.gradle.internal.dsl.DefaultConfig

plugins {
    androidApp()
    kotlinAndroid()
    kotlinKapt()
    safeargs()
}

android {

    defaultConfig {
        applicationId = Apps.applicationId
        versionCode = Apps.versionCode
        versionName = Apps.versionName

        val strValue =
            getDynamicFeatureModules().toSet().joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
        buildConfigField("String[]", name, strValue)
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    // Each feature module that is included in settings.gradle.kts is added here as dynamic feature
    setDynamicFeatures(getDynamicFeatureModules().toMutableSet())

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.google.android.material:material:1.2.0-alpha05")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")
    implementation("androidx.paging:paging-runtime:2.1.0")
    implementation("com.google.android.play:core:1.10.1")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.fragment:fragment-ktx:1.2.4")
    implementation("androidx.activity:activity-ktx:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")

    implementation("androidx.test.ext:junit-ktx:1.1.1")
    implementation("androidx.test.ext:truth:1.2.0")

    // Libraries which can be re-used in other modules should use the `api` keyword.
    // This way they can be shared with dependent feature modules.

    api("androidx.navigation:navigation-fragment-ktx:2.2.2")
    api("androidx.navigation:navigation-ui-ktx:2.2.2")
    api("androidx.navigation:navigation-runtime-ktx:2.2.2")
}

fun DefaultConfig.buildConfigField1(name: String, value: Set<String>) {
    // Generates String that holds Java String Array code
    val strValue =
        value.joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
    buildConfigField("String[]", name, strValue)
}

