plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("com.maksim88:PasswordEditText:v0.9")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")

    //circle_image_view
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.rishabhharit.roundedimageview:RoundedImageView:0.8.4")

    //rx_image_picker
    implementation ("com.mlsdev.rximagepicker:library:2.2.1")

    //recycler_view
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.mlsdev.animatedrv:library:2.0.0")

    //view_pager
    implementation ("androidx.viewpager2:viewpager2:1.1.0-beta02")

    //image picker
    implementation ("com.github.Drjacky:ImagePicker:2.3.22")

    // spinner
    implementation ("com.github.chivorns:smartmaterialspinner:1.5.0")

    // compress image
    implementation ("id.zelory:compressor:3.0.1")

    // size
    implementation ("com.intuit.sdp:sdp-android:1.0.6")
    implementation ("com.intuit.ssp:ssp-android:1.0.6")

    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-messaging:23.3.1")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")

    //rx
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.18")
    implementation ("io.reactivex.rxjava2:rxkotlin:2.1.0")


    implementation ("com.airbnb.android:lottie:6.2.0")
    implementation ("com.github.thekhaeng:pushdown-anim-click:1.1.1")


    //edit_text_lib
    implementation ("ss.anoop:awesome-textinput-layout:1.0.3")
    implementation ("com.maksim88:PasswordEditText:v0.9")

    //drawer
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}