plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        viewBinding= true
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
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // RecyclerView dependency
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Coroutines dependency
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // MPAndroidChart library for charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // TensorFlow Lite for on-device machine learning
    implementation("org.tensorflow:tensorflow-lite:2.16.1") // Check for updates
//
//    // CameraX dependencies for camera access
//    implementation("androidx.camera:camera-core:1.3.4")
//    implementation("androidx.camera:camera-camera2:1.3.4")
//    implementation("androidx.camera:camera-lifecycle:1.3.4")
//    implementation("androidx.camera:camera-view:1.3.4") // Use stable version
////    implementation(":opencv")
    // Face Detection
    implementation("com.google.mlkit:face-detection:16.1.5")

// CameraX
    var camerax_version = "1.3.0-alpha04"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

// CameraSource
    implementation ("com.google.android.gms:play-services-vision-common:19.1.3")



}
