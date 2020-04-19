plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {

    compileSdkVersion(Apps.compileSdk)
    defaultConfig {
        applicationId = Apps.applicationId
        minSdkVersion(Apps.minSdk)
        targetSdkVersion(Apps.targetSdk)
        versionCode = Apps.versionCode
        versionName = Apps.versionName
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    dataBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            noStdlib = true
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf("-Xallow-result-return-type")
        }
    }
}

dependencies {
    // Libraries which can be re-used in other modules should use the `api` keyword.
    // This way they can be shared with dependent feature modules.
    implementation(kotlin("stdlib-jdk7"))
    api(Lib.material)
    api(Lib.constraintLayout)
    api(Lib.coroutinesLifecycle)
    api(Lib.coroutinesLivedata)
    api(Lib.coroutinesViewmodel)
    api(Lib.paging)
    api(Lib.playCore)
    api(Lib.coil)
    api(Lib.coreKtx)
    api(Lib.fragmentKtx)
    api(Lib.coroutines)
    api(Lib.navigationUiKtx)
    api(Lib.navigationFragmentKtx)
    api(Lib.navigationRuntimeKtx)
}