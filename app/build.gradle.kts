plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.prathambudhwani.diagnosis"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.prathambudhwani.diagnosis"
        minSdk = 28
        //noinspection EditedTargetSdkVersion
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.mlkit:face-detection:16.1.5")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-common:19.0.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}