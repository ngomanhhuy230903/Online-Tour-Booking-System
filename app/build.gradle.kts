plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tourbooking"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tourbooking"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // === THAY ĐỔI 1: BẬT HỖ TRỢ MULTIDEX ===
        multiDexEnabled = true
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

    // === THAY ĐỔI 2: CHUẨN HÓA CÁC THƯ VIỆN CỐT LÕI ===
    // Sử dụng các phiên bản cụ thể, ổn định thay cho libs.* để tránh xung đột
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // --- Firebase BOM (Giữ các thư viện Firebase đồng bộ) ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // --- Database Local (Room) ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // --- Thanh toán (Stripe) ---
    implementation("com.stripe:stripe-android:20.43.0")

    // --- Tạo mã QR ---
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // --- Các thư viện hữu ích khác của Android Jetpack ---
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2") // Dùng phiên bản tương thích
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2") // Dùng phiên bản tương thích

    // === THAY ĐỔI 3: THÊM THƯ VIỆN MULTIDEX ===
    implementation("androidx.multidex:multidex:2.0.1")

    // --- Thư viện Test ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
// Thư viện Glide để tải ảnh
    implementation("com.github.bumptech.glide:glide:4.16.0")
}