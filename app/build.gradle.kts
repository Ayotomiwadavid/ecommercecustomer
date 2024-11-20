plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    namespace = "tech.azurestar.kmp.ecommercecustomer"
    compileSdk = 35

    defaultConfig {
        applicationId = "tech.azurestar.kmp.ecommercecustomer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    //Supabase
    implementation(platform(libs.bom))
    implementation(libs.compose.auth)
    implementation(libs.compose.auth.ui)
    implementation(libs.coil3.integration)
    implementation(libs.ktor.client.cio)
    implementation(libs.postgrest.kt)
    implementation(libs.auth.kt)

    //Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.client.core)
    implementation(libs.coil.network.ktor2)

    // Icons
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.realtime.kt)

    implementation(libs.coil.compose)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Web Socket
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("io.github.aakira:napier:2.6.1")
// Core Ktor dependencies
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4") // CIO engine for HTTP and WebSocket
    implementation("io.ktor:ktor-client-websockets:2.3.4") // WebSocket support
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4") // JSON serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4") // Kotlinx serialization support

}