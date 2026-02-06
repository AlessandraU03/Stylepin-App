import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.jetbrainsKotlinSerialization)
}

android {
    namespace = "com.ale.stylepin"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.ale.stylepin"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    secrets {
        propertiesFileName = "local.properties"
        defaultPropertiesFileName = "local.defaults.properties"
        ignoreList.add("sdk.dir")
    }


    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            dimension = "environment"
            // Se elimina buildConfigField manual
            resValue("string", "app_name", "StylePin (DEV)")
        }

        create("prod") {
            dimension = "environment"
            // Se elimina buildConfigField manual
            resValue("string", "app_name", "StylePin")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)   //viewModel()
    implementation(libs.com.squareup.retrofit2.retrofit)        // Retrofit
    implementation(libs.com.squareup.retrofit2.converter.json)  // JSON
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.navigation.compose)                // IO
    implementation(libs.androidx.compose.material.icons.extended)   // Icons
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

}
