import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension

pluginManager.withPlugin("com.android.base") {
    configure<CommonExtension> {
        compileSdk = 37

        defaultConfig.apply {
            minSdk = 30
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        lint.apply {
            abortOnError = false
            disable += "UseCompoundDrawables"
        }
    }
    extensions.findByType<ApplicationExtension>()?.defaultConfig?.targetSdk = 37
    extensions.findByType<LibraryExtension>()?.lint?.targetSdk = 37
}

dependencies {
//    add("lintChecks", project(Module.Core.rules))
}
