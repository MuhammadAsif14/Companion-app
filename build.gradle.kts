// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        // Make sure that you have the following two repositories
        gradlePluginPortal()
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
