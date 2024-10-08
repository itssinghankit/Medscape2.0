// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

//    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
}
buildscript {
    extra["kotlinVersion"] = "1.8.0"
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        //navigation
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }

}

