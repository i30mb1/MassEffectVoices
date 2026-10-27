@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.CommonExtension

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<CommonExtension> {
    buildFeatures.compose = true
}

composeCompiler {
//    metricsDestination = layout.buildDirectory.dir("compose_metrics")
//    reportsDestination = layout.buildDirectory.dir("compose_metrics")
}

dependencies {
    add("implementation", platform(libs.composeBom))
    add("implementation", libs.bundles.compose)
    add("debugImplementation", libs.composeTooling)
}
