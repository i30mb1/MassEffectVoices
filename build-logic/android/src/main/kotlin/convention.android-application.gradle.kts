plugins {
    id("com.android.application")
    id("convention.android-base")
    id("convention.kotlin-base")
}

android {
    buildTypes {
        getByName("debug") {
            matchingFallbacks += "release"
        }
    }
}
