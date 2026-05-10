plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.nicolasght.salut"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nicolasght.salut"
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "2.0.4"
    }

    signingConfigs {
        create("release") {
            val ksPath = System.getenv("SALUT_KEYSTORE_PATH") ?: "${rootDir}/keystore/salut.jks"
            storeFile = file(ksPath)
            storePassword = System.getenv("SALUT_KEYSTORE_PASSWORD") ?: "salutapp2026"
            keyAlias = System.getenv("SALUT_KEY_ALIAS") ?: "salut"
            keyPassword = System.getenv("SALUT_KEY_PASSWORD") ?: "salutapp2026"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
}
