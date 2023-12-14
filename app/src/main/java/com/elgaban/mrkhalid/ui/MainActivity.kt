package com.elgaban.mrkhalid.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.ActivityMainBinding
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.NoInternetDialog
import com.elgaban.mrkhalid.viewModel.AuthViewModel
import com.elgaban.mrkhalid.viewModel.ConnectionViewModel
import com.elgaban.mrkhalid.viewModel.LoggedInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    private val connectionViewModel: ConnectionViewModel by viewModels()

    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        if (loggedInViewModel.getProfileCompleted() == "1") {
            authViewModel.getUserData()
        } else if (loggedInViewModel.getProfileCompleted() == "2") {
            Log.d("complete", "complete")
        }

        noInternetDialog = NoInternetDialog(this)
        noInternetDialog!!.setCancelable(false)
        noInternetDialog!!.window?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))
        )
        connectionViewModel.isOnline.observe(this) { isOnline ->
            if (isOnline) {
                noInternetDialog!!.hide()
            } else {
                noInternetDialog!!.show()
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                authViewModel.userData.collect {
                    if (it.isLoading) {
                        dataBinding.animationLoading.visibility = View.VISIBLE
                    }
                    if (it.error.isNotBlank()) {
                        dataBinding.animationLoading.visibility = View.GONE
                        showToastError(it.error)
                    }
                    it.data?.let { student ->
                        if (student.profileCompleted == "1") {
                            Log.d("complete", "not completed ask to complete")
                        } else if (student.profileCompleted == "2") {
                            Log.d("complete", "completed and save to session")
                        }
                        dataBinding.animationLoading.visibility = View.GONE
                    }
                }
            }
        }
    }
}