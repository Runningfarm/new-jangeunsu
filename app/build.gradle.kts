plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "kr.ac.hs.farm"
    compileSdk = 35

    defaultConfig {
        applicationId = "kr.ac.hs.farm"
        minSdk = 24
        targetSdk = 35
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

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")               // 안정적 최신 버전
    implementation("androidx.appcompat:appcompat:1.7.0")           // 1.7.0은 최신 버전 아님, 1.6.1 ~ 1.7.0 중 선택 가능 (현재 1.7.0 안정판)
    implementation("com.google.android.material:material:1.9.0")    // Material 1.12.0은 미출시, 현재 1.9.0 최신 안정 버전
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // 2.2.1보다 안정성 및 호환성 좋음
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")  // Google Maps SDK
    implementation("com.google.android.gms:play-services-location:21.0.1")  // 위치(위치 추적 등)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")      // 최신 안정버전
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // 최신 안정버전

}
