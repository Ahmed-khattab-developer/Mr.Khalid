package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.elgaban.mrkhalid.R

class Splash : AppCompatActivity() {

    private var mProgressBar: ProgressBar? = null
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mProgressBar = findViewById(R.id.progress_bar)
    }

    override fun onStart() {
        super.onStart()
        startloading()
    }

    override fun onResume() {
        super.onResume()
        startloading()
    }

    private fun startloading() {
        // Start long running operation in a background thread
        Thread {
            while (progressStatus < 100) {
                progressStatus += 5
                // Update the progress bar and display the
                //current value in the text view
                Handler(Looper.getMainLooper()).post {
                    mProgressBar!!.progress = progressStatus
                    if (progressStatus == 100) {
                        startActivity(Intent(this@Splash, SignInActivity::class.java))
                        finish()
                    }
                }
                try {
                    // Sleep for 500 milliseconds.
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

}