plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.elgaban.mrkhalid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.elgaban.mrkhalid"
        minSdk = 21
        targetSdk = 34
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")

    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    kapt("com.google.dagger:hilt-compiler:2.44")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //pass edit text
    implementation("com.github.scottyab:showhidepasswordedittext:0.8")

    //circle image view
    implementation("com.rishabhharit.roundedimageview:RoundedImageView:0.8.4")

    //recycler_view
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //view_pager
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")


    //image picker
    implementation("com.github.Drjacky:ImagePicker:2.3.22")

    // spinner
    implementation("com.github.chivorns:smartmaterialspinner:1.5.0")

    // size
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    //glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    //lottie
    implementation("com.airbnb.android:lottie:6.2.0")

    //push down
    implementation("com.github.thekhaeng:pushdown-anim-click:1.1.1")

    //drawer
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    /*    coroutines support for firebase operations */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}