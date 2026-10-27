import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("extensions")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn", "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
