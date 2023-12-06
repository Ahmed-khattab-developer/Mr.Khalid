package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.ActivitySplashBinding
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PROFILE_COMPLETED

class SplashActivity : AppCompatActivity() {

    private lateinit var databinding: ActivitySplashBinding
    private lateinit var iSessionManagement: SessionManagement
    private var mProgressBar: ProgressBar? = null
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
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
        Thread {
            while (progressStatus < 100) {
                progressStatus += 5
                Handler(Looper.getMainLooper()).post {
                    mProgressBar!!.progress = progressStatus
                    if (progressStatus == 100) {
                        if (iSessionManagement.isLoggedIn()) {
                            if (iSessionManagement.getUserDetails()[PROFILE_COMPLETED] == "0") {
                                val intent =
                                    Intent(this@SplashActivity, AboutYouActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            } else if (iSessionManagement.getUserDetails()[PROFILE_COMPLETED] == "1") {
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}