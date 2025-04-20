plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "dev.shchuko.marinescreen"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.shchuko.marinescreen"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    debugImplementation(libs.androidx.ui.tooling)
    
    implementation(libs.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.compose.androidx.material3)
    implementation(libs.compose.androidx.ui)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.datetime)
    implementation(platform(libs.compose.bom))
    implementation(project(":data"))
    implementation(project(":domain"))

    kapt(libs.hilt.compiler)
}