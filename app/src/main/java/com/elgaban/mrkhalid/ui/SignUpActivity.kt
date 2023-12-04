package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.utils.appUtils.AppConstant.Constants.STUDENT
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText
import com.thekhaeng.pushdownanim.PushDownAnim

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var userNameEditText: AppCompatEditText? = null
    private var emailEditText: AppCompatEditText? = null
    private var passwordEditText: ShowHidePasswordEditText? = null
    private var signUpButton: AppCompatButton? = null
    private var login: AppCompatTextView? = null
    private var animationLoading: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /////*     initialize view   */////
        login = findViewById(R.id.id_SignIn_TextView)
        userNameEditText = findViewById(R.id.userName_EditText)
        emailEditText = findViewById(R.id.email_EditText)
        passwordEditText = findViewById(R.id.password_EditText)
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
        val username: String = userNameEditText?.text.toString()
        val email: String = emailEditText?.text.toString()
        val password: String = passwordEditText?.text.toString()

        /////*   Check if email and password written and valid   */////
        if (!validate(username, email, password)) {
            return
        } else {
            hideKeyboard()
            if (Utils.hasInternetConnection(this)) {
                signup(username, email, password)
            } else {
                showToastNoInternet(this)
            }
        }
    }

    private fun validate(userName: String, email: String, password: String): Boolean {
        var valid = true
        if (userName.isEmpty()) {
            userNameEditText?.error = getString(R.string.validusername)
            userNameEditText?.isFocusable = true
            valid = false
        } else {
            userNameEditText?.error = null
        }
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

    private fun signup(userName: String, email: String, password: String) {
        animationLoading?.visibility = View.VISIBLE
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // success
                    val student = Student(
                        it.result?.user?.uid!!, userName, "", "",
                        email, password, "", "", "", "", "0"
                    )

                    FirebaseFirestore.getInstance().collection(STUDENT)
                        .document(it.result?.user?.uid!!).set(student, SetOptions.merge())
                        .addOnSuccessListener {
                            startActivity(Intent(this, AboutYouActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            showToastError(this@SignUpActivity, e.message.toString())
                        }

                } else {
                    // error
                    animationLoading?.visibility = View.GONE
                    showToastError(this, it.exception?.message.toString())
                }
            }
    }
}