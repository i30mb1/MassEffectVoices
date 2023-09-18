plugins {
    id("com.android.dynamic-feature")
    id("convention.android-base")
    id("convention.kotlin-base")
}

android {
    namespace = "$applicationID.${project.path.replace("-", "").replace(":", ".").drop(1)}"
}
