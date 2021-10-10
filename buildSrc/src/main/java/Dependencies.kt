// when we have all version's here how can we know there is new version available for our lib?
object Apps {
    const val compileSdk = 31
    const val applicationId = "n7.mev"
    const val minSdk = 23
    const val targetSdk = 31
    const val versionCode = 16
    const val versionName   = "16"
}

object Versions {
    const val gradlePlugin = "7.0.2"
    const val kotlin = "1.5.30"
    const val lifecycle = "2.4.0-beta01"
}

object Lib {
    const val coreKtx = "androidx.core:core-ktx:1.6.0" // Write more concise, idiomatic Kotlin code.
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.0"
    const val material = "com.google.android.material:material:1.5.0-alpha03" // Build beautiful products, faster.
    const val coil = "io.coil-kt:coil:0.13.0"
    const val playCore = "com.google.android.play:core:1.10.2"
    const val playCoreKtx = "com.google.android.play:core-ktx:1.8.1"
    const val exoPlayer = "com.google.android.exoplayer:exoplayer-core:2.14.2"

    // --- Coroutines ---
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
    const val coroutinesLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}" // lifecycleScope + launchWhenResumed and ets.
    const val coroutinesLivedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}" // liveData (LiveData + coroutines)
    const val coroutinesViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}" // viewModelScope + savedStateHandle
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.6" // easy fragment transaction + by viewModels()
    const val activityKtx = "androidx.activity:activity-ktx:1.3.1" // on BackPress support for Fragment

    // --- Dagger ---
    const val dagger = "com.google.dagger:dagger:2.25.2"
    const val daggerAnnotation = "com.google.dagger:dagger-compiler:2.25.2"
    const val daggerAssisted = "com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2"

    // --- Retrofit ---
    const val retrofit               = "com.squareup.retrofit2:retrofit:2.7.1"
    const val retrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:2.7.1"
    const val retrofitInterceptor    = "com.squareup.okhttp3:logging-interceptor:4.3.1"

    // --- Moshi ---
    const val moshi        = "com.squareup.moshi:moshi:1.9.2" // It makes it easy to parse JSON into Kotlin objects
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:1.9.2" // Add codegen to moshi (generating by using @JsonClass(generateAdapter = true))
    const val moshiKotlin  = "com.squareup.moshi:moshi-kotlin:1.9.2" // Add reflection to moshi (better not to use : 2.5 MB)
    const val moshiAdapter = "com.squareup.moshi:moshi-adapters:1.9.2"

}
