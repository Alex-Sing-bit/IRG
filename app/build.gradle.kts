plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.irg0"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.irg0"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:1.4.0-alpha04")
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-camera2:1.4.0-alpha04")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha04")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:1.4.0-alpha04")

    implementation("androidx.camera:camera-extensions:1.4.0-alpha04")

    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")
}