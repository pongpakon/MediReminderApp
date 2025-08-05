plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // เพิ่ม plugin สำหรับ Compose Compiler เพื่อแก้ปัญหา Unresolved reference
    id("org.jetbrains.kotlin.plugin.compose")
    // เพิ่ม plugin สำหรับ Google Services เพื่อให้ใช้ Firebase ได้
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.medireminderapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.medireminderapp"
        minSdk = 24
        targetSdk = 36
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

    kotlin {
        // ใช้ compilerOptions เพื่อตั้งค่า jvmTarget
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
            freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ใช้ Version Catalog เพื่ออ้างอิง Firebase BoM
    implementation(platform(libs.firebase.bom))

    // ใช้ Version Catalog สำหรับ Firebase Firestore
    implementation(libs.firebase.firestore)

    // Dependencies ของโปรเจกต์เดิม
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Dependencies สำหรับการทดสอบ (Test Dependencies)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
