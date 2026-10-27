plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.composeCompilerGradlePlugin)
    implementation(project(":extensions"))
    implementation(project(":kotlin"))
}