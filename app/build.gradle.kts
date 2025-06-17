plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Đảm bảo bạn có dòng này
}

android {
    namespace = "com.example.tourbooking"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tourbooking"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // --- Firebase (Phiên bản Java, cú pháp Kotlin DSL) ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // --- Database Local (Room) ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    // Quan trọng: Với code Java, chúng ta vẫn dùng annotationProcessor
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // --- Thanh toán (Stripe) ---
    implementation("com.stripe:stripe-android:20.43.0")

    // --- Tạo mã QR ---
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // --- Các thư viện hữu ích khác của Android Jetpack ---
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}