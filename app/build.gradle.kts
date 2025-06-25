import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.vectordrawable)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.perf)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.android.test.junit)
    androidTestImplementation(libs.android.test.espresso.core)

    implementation(libs.shapeofview)

    implementation(libs.glide.core)
    kapt(libs.glide.compiler)
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0") {
        exclude(group = "glide-parent")
    }

    implementation(libs.butterknife.core)
    kapt(libs.butterknife.compiler)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava2)
    implementation(libs.gson.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.jackson)
    implementation(libs.retrofit.logging.interceptor)

    implementation(libs.rxjava.core)
    implementation(libs.rxandroid.core)
    implementation(libs.rxkotlin.core)

    implementation(libs.dagger.core)
    implementation(libs.dagger.android)
    implementation(libs.dagger.android.support)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.android.processor)

//    compileOnly(libs.jsr250.api)
//    implementation(libs.javax.inject)

    implementation(files("libs/skeleton-1.1.2.aar")) // manual

    implementation(libs.shimmerlayout)
    implementation(libs.facebook.shimmer)
    implementation(libs.skeletonlayout)

    implementation(libs.spinkit)
    implementation(libs.image.slider)

    implementation(libs.zoomage)
    implementation(libs.pinch.to.zoom)

    implementation(libs.round.corner)
    implementation(libs.bottom.sheet)
    implementation(libs.simple.rating.bar)


    implementation(libs.material.search.bar)
    implementation(libs.floating.search.view)

    implementation(libs.parceler.api)
    kapt(libs.parceler.impl)

    implementation(libs.rey.material)

    implementation(libs.androidx.window)
}