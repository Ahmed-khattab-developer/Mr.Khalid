package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.google.firebase.auth.FirebaseAuth
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText
import com.thekhaeng.pushdownanim.PushDownAnim

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private var signInButton: AppCompatButton? = null
    private var emailEditText: AppCompatEditText? = null
    private var passwordEditText: ShowHidePasswordEditText? = null
    private var forgetPasswordTextView: AppCompatTextView? = null
    private var signUpTextView: AppCompatTextView? = null
    private var animationLoading: LottieAnimationView? = null

    private lateinit var iSessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        iSessionManagement = SessionManagement(this)

        /////*     initialize view   */////
        signInButton = findViewById(R.id.id_signIn_Button)
        emailEditText = findViewById(R.id.id_Email_EditText)
        passwordEditText = findViewById(R.id.id_password_EditText)
        forgetPasswordTextView = findViewById(R.id.id_forgetPassword_TextView)
        signUpTextView = findViewById(R.id.id_SignUP_TextView)
        animationLoading = findViewById(R.id.animation_loading)
        checkPassLength()

        /////*     On Click         */////
        PushDownAnim.setPushDownAnimTo(signInButton).setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(forgetPasswordTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(signUpTextView).setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v === forgetPasswordTextView) {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        if (v === signUpTextView) {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        if (v === signInButton) {
            signInFunction()
        }
    }

    private fun signInFunction() {
        /////*   Get  Email && Password    */////
        val emailS: String = emailEditText?.text.toString()
        val passwordS = passwordEditText?.text.toString()
        /////*   Check if email and password written  valid   */////
        if (!validate(emailS, passwordS)) {
            return
        } else {
            hideKeyboard()
            if (Utils.hasInternetConnection(this)) {
                login(emailS, passwordS)
            } else {
                showToastNoInternet(this)
            }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        var valid = true
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText?.error = getString(R.string.validemail)
            emailEditText?.isFocusable = true
            valid = false
        } else {
            emailEditText?.error = null
        }
        if (password.isEmpty() || password.length < 6 || password.length > 12) {
            passwordEditText?.error = getString(R.string.validpassword)
            passwordEditText?.isFocusable = true
            valid = false
        } else {
            passwordEditText?.error = null
        }
        return valid
    }

    private fun checkPassLength() {
        passwordEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
    }

    private fun login(email: String, password: String) {
        animationLoading?.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // success
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // error
                    animationLoading?.visibility = View.GONE
                    showToastError(this, getString(R.string.incorrect_email_or_password))
                }
            }
    }

}