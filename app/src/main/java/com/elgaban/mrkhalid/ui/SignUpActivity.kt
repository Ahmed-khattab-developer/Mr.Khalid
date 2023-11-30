package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.fontsmaterialuiux.cairoButton
import com.elgaban.mrkhalid.utils.fontsmaterialuiux.cairoEditText
import com.elgaban.mrkhalid.utils.fontsmaterialuiux.cairoTextView
import com.google.firebase.auth.FirebaseAuth
import com.thekhaeng.pushdownanim.PushDownAnim

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var emailEditText: cairoEditText? = null
    private var passwordEditText: cairoEditText? = null
    private var signUpButton: cairoButton? = null
    private var login: cairoTextView? = null
    private var animationLoading: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /////*     initialize view   */////
        login = findViewById(R.id.id_SignIn_TextView)
        emailEditText = findViewById(R.id.id_Email_EditText)
        passwordEditText = findViewById(R.id.id_password_EditText)
        signUpButton = findViewById(R.id.id_signUp_Button)
        animationLoading = findViewById(R.id.animation_loading)
        checkPassLength()

        /////*     On Click         */////
        PushDownAnim.setPushDownAnimTo(login).setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(signUpButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v === login) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        if (v === signUpButton) {
            signUpFunction()
        }

    }

    private fun signUpFunction() {

        /////*   Get  Email  && Name  && Password    */////
        val email: String = emailEditText?.text.toString()
        val password: String = passwordEditText?.text.toString()

        /////*   Check if email and password written and valid   */////
        if (!validate(email, password)) {
            return
        } else {
            hideKeyboard()
            if (Utils.hasInternetConnection(this)) {
                signup(email, password)
            } else {
                showToastNoInternet(this)
            }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        var valid = true
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    private fun signup(email: String, password: String) {
        animationLoading?.visibility = View.VISIBLE
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // success
                    startActivity(Intent(this, AboutYouActivity::class.java))
                    finish()
                } else {
                    // error
                    animationLoading?.visibility = View.GONE
                    showToastError(this, "مشكلة في تسجيل الدخول حاول لاحقا")
                }
            }
    }

}