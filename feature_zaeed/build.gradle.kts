plugins {
    dynamicFeature()
    kotlinAndroid()
}

android {
    defaultConfig {
        versionCode = Apps.versionCode
        versionName = Apps.versionName
    }
}

dependencies {
    implementation(project(ModuleDependency.APP))
}
