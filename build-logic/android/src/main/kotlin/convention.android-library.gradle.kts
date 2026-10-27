plugins {
    id("com.android.library")
    id("convention.android-base")
    id("convention.kotlin-base")
}

android {
    namespace = "$applicationID.${project.path.replace("-", "").replace(":", ".").drop(1)}"
    buildTypes {
        getByName("debug") {
            matchingFallbacks += "release"
        }
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
}

androidComponents {
    beforeVariants { variantBuilder ->
        if (variantBuilder.buildType == "debug") {
            if (project.name == "ui" && project.path.contains(":core:").not()) return@beforeVariants
            variantBuilder.enable = false
        }
    }
}
