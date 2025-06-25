import java.text.SimpleDateFormat
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)

}

android {
    namespace = "id.fajarproject.roommovie"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "id.fajarproject.roommovie"
        minSdk = 23
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1-build${getDate()}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

fun getDate(): String {
    val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    return sdf.format(Date())
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("com.google.android.material:material:1.6.0-alpha02")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    implementation("com.google.firebase:firebase-crashlytics:17.1.1")
    implementation("com.google.firebase:firebase-analytics:17.4.4")
    implementation("com.google.firebase:firebase-analytics-ktx:17.4.4")
    implementation("com.google.firebase:firebase-perf:19.0.8")

    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("com.github.florent37:shapeofview:1.0.7")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0") {
        exclude(group = "glide-parent")
    }

    implementation("com.jakewharton:butterknife:10.2.3")
    kapt("com.jakewharton:butterknife-compiler:10.2.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation("io.reactivex.rxjava2:rxjava:2.2.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    implementation("com.google.dagger:dagger:2.51.1")
    implementation("com.google.dagger:dagger-android:2.51.1")
    implementation("com.google.dagger:dagger-android-support:2.51.1")
    kapt ("com.google.dagger:dagger-compiler:2.51.1")
    kapt ("com.google.dagger:dagger-android-processor:2.51.1")

    compileOnly("javax.annotation:jsr250-api:1.0")
    implementation("javax.inject:javax.inject:1")

    implementation(files("libs/skeleton-1.1.2.aar"))
    implementation("io.supercharge:shimmerlayout:2.1.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.faltenreich:skeletonlayout:4.0.0")

    implementation("com.github.ybq:Android-SpinKit:1.2.0")

    implementation("com.github.smarteist:Android-Image-Slider:1.4.0")

    implementation("com.jsibbold:zoomage:1.3.1")
    implementation("com.bogdwellers:pinchtozoom:0.1")

    implementation("com.akexorcist:RoundCornerProgressBar:2.0.3")

    implementation("com.github.ome450901:SimpleRatingBar:1.5.0")

    implementation("com.github.andrefrsousa:SuperBottomSheet:1.5.0")

    implementation("com.github.mancj:MaterialSearchBar:0.8.5")
    implementation("com.github.arimorty:floatingsearchview:2.1.1")

    implementation("org.parceler:parceler-api:1.1.12")
    kapt("org.parceler:parceler:1.1.12")

    implementation("com.github.rey5137:material:1.3.1")

    implementation("androidx.window:window:1.4.0")
}