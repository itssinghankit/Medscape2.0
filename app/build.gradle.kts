import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    //hilt
//    id("kotlin-kapt")
//    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    alias(libs.plugins.google.gms.google.services)

    //maps
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    //navigation
    id("androidx.navigation.safeargs.kotlin")

}


android {
    namespace = "com.example.medscape20"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medscape20"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //For protecting api keys from version controls systems
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream());

        val baseUrl = properties.getProperty("BASE_URL")
        val apiKey = properties.getProperty("NEWS_API_KEY")

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "NEWS_API_KEY", "\"$apiKey\"")
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
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    //timber
    implementation(libs.timber)

    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //hilt
//    implementation(libs.hilt)
//    kapt(libs.hilt.android.compiler)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    //firebase
    implementation(libs.firebase.bom)
    implementation(libs.google.firebase.auth)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    //maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

}
kapt {
    correctErrorTypes = true
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

