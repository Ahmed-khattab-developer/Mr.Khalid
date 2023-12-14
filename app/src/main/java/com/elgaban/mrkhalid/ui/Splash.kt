package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.ActivitySplashBinding
import com.elgaban.mrkhalid.viewModel.LoggedInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Splash : AppCompatActivity() {

    private lateinit var databinding: ActivitySplashBinding
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
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
                    databinding.progressBar.progress = progressStatus
                    if (progressStatus == 100) {
                        if (loggedInViewModel.getIsLoggedIn()) {
                            if (loggedInViewModel.getProfileCompleted() == "0") {
                                val intent =
                                    Intent(this@Splash, AboutYouActivity::class.java)
                                intent.putExtra("name", loggedInViewModel.getName())
                                intent.putExtra("email", loggedInViewModel.getEmail())
                                intent.putExtra("password", loggedInViewModel.getPassword())
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            } else if (loggedInViewModel.getProfileCompleted() == "1") {
                                val intent = Intent(this@Splash, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            } else if (loggedInViewModel.getProfileCompleted() == "2") {
                                val intent = Intent(this@Splash, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this@Splash, SignInActivity::class.java)
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
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}