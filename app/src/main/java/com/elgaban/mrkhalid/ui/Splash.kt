package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PROFILE_COMPLETED

class Splash : AppCompatActivity() {

    private var mProgressBar: ProgressBar? = null
    private var progressStatus = 0

    private lateinit var iSessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        iSessionManagement = SessionManagement(this)
        mProgressBar = findViewById(R.id.progress_bar)
    }

    override fun onStart() {
        super.onStart()
        startLoading()
    }

    override fun onResume() {
        super.onResume()
        startLoading()
    }

    private fun startLoading() {
        // Start long running operation in a background thread
        Thread {
            while (progressStatus < 100) {
                progressStatus += 5
                // Update the progress bar and display the
                //current value in the text view
                Handler(Looper.getMainLooper()).post {
                    mProgressBar!!.progress = progressStatus
                    if (progressStatus == 100) {
                        if (iSessionManagement.isLoggedIn()) {
                            if (iSessionManagement.getUserDetails()[PROFILE_COMPLETED] == "0") {
                                val intent = Intent(this@Splash, AboutYouActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            } else if (iSessionManagement.getUserDetails()[PROFILE_COMPLETED] == "1") {
                                val intent = Intent(this@Splash, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val intent = Intent(this@Splash, SignInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }
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