plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //navigation safe args
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.ly.wvp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.ly.wvp"
        minSdk = 30
        targetSdk = 33
        versionCode = 20232201
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // Navigation
    val navVersion = "2.5.2"
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    //GSYPlayer
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-java:v8.4.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-arm64:v8.4.0-release-jitpack")

    //CalendarView
//    implementation("com.haibin:calendarview:3.7.1")

    //Json
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.github.donkingliang:GroupedRecyclerViewAdapter:2.4.3")
}