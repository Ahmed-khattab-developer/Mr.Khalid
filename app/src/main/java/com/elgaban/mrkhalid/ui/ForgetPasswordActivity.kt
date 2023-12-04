package com.elgaban.mrkhalid.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.airbnb.lottie.LottieAnimationView
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastSuccess
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.thekhaeng.pushdownanim.PushDownAnim

class ForgetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var emailEditText: AppCompatEditText? = null
    private var doneButton: AppCompatButton? = null
    private var animationLoading: LottieAnimationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        emailEditText = findViewById(R.id.id_Email_EditText)
        doneButton = findViewById(R.id.id_done_Button)
        animationLoading = findViewById(R.id.animation_loading)

        PushDownAnim.setPushDownAnimTo(doneButton).setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v === doneButton) {
            forgetFunction()
        }
    }

    private fun forgetFunction() {
        /////*   Get  Email && Password    */////
        val emailS: String = emailEditText?.text.toString()
        /////*   Check if email and password written  valid   */////
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
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText?.error = getString(R.string.validemail)
            emailEditText?.isFocusable = true
            valid = false
        } else {
            emailEditText?.error = null
        }
        return valid
    }

    private fun forget(email: String) {
        animationLoading?.visibility = View.VISIBLE
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                animationLoading?.visibility = View.GONE
                if (it.isSuccessful) {
                    showToastSuccess(this@ForgetPasswordActivity, getString(R.string.send_email))
                } else {
                    showToastError(this@ForgetPasswordActivity, it.exception?.message.toString())
                }
            }
    }
}