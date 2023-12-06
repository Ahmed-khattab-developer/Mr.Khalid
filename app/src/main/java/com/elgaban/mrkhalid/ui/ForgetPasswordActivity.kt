package com.elgaban.mrkhalid.ui

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.ActivityForgetPasswordBinding
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastSuccess
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.thekhaeng.pushdownanim.PushDownAnim

class ForgetPasswordActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password)

        PushDownAnim.setPushDownAnimTo(dataBinding.doneButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v === dataBinding.doneButton) {
            forgetFunction()
        }
    }

    private fun forgetFunction() {
        val emailS: String = dataBinding.emailEditText.text.toString()
        if (!validate(emailS)) {
            return
        } else {
            hideKeyboard()
            if (Utils.hasInternetConnection(this)) {
                forget(emailS)
            } else {
                showToastNoInternet(this)
            }
        }
    }

    private fun validate(email: String): Boolean {
        var valid = true
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            dataBinding.emailEditText.error = getString(R.string.validEmail)
            dataBinding.emailEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.emailEditText.error = null
        }
        return valid
    }

    private fun forget(email: String) {
        dataBinding.animationLoading.visibility = View.VISIBLE
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                dataBinding.animationLoading.visibility = View.GONE
                if (it.isSuccessful) {
                    showToastSuccess(this@ForgetPasswordActivity, getString(R.string.sendEmail))
                } else {
                    showToastError(this@ForgetPasswordActivity, it.exception?.message.toString())
                }
            }
    }
}