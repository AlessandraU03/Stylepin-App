import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    // Activa Hilt y KSP
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
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

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
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

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
    ignoreList.add("sdk.dir")
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
    implementation(libs.androidx.compose.material)

    implementation(libs.androidx.compose.ui.text.google.fonts)      // G Fonts
    implementation(libs.androidx.lifecycle.viewmodel.compose)       // viewModel()
    implementation(libs.com.squareup.retrofit2.retrofit)            // Retrofit
    implementation(libs.com.squareup.retrofit2.converter.json)      // JSON
    implementation(libs.io.coil.kt.coil.compose)                    // Coil
    implementation(libs.androidx.navigation.compose)                // Navigation
    implementation(libs.androidx.compose.material.icons.extended)   // Icons extendend
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)                    // Integración con Jetpack Compose
    ksp(libs.hilt.compiler)

    // Biometric & Camera
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
