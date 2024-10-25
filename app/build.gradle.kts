plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.companionek"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.companionek"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")

    // RecyclerView dependency
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Coroutines dependency
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // MPAndroidChart library for charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // TensorFlow Lite for on-device machine learning
    implementation("org.tensorflow:tensorflow-lite:2.16.1") // Check for updates

    implementation("com.google.guava:guava:27.1-android")

    // Face Detection
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    // CameraX
    val cameraxversion = "1.3.0-alpha04"
    implementation("androidx.camera:camera-core:$cameraxversion")
    implementation("androidx.camera:camera-camera2:$cameraxversion")
    implementation("androidx.camera:camera-lifecycle:$cameraxversion")
    implementation("androidx.camera:camera-video:$cameraxversion")
    implementation("androidx.camera:camera-view:$cameraxversion")
    implementation("androidx.camera:camera-mlkit-vision:$cameraxversion")
    implementation("androidx.camera:camera-extensions:$cameraxversion")

    // CameraSource
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.5.0"))

    // Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.0.0")


    // Material Calendar View
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")



}
