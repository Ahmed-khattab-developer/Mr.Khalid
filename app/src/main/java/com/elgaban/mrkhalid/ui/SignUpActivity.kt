package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.databinding.ActivitySignUpBinding
import com.elgaban.mrkhalid.utils.appUtils.AppConstant.Constants.STUDENT
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.thekhaeng.pushdownanim.PushDownAnim

class SignUpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivitySignUpBinding
    private lateinit var iSessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        iSessionManagement = SessionManagement(this)
        checkPassLength()
        PushDownAnim.setPushDownAnimTo(dataBinding.SignInTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.signUpButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v === dataBinding.SignInTextView) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        if (v === dataBinding.signUpButton) {
            signUpFunction()
        }
    }

    private fun signUpFunction() {
        val username: String = dataBinding.userNameEditText.text.toString()
        val email: String = dataBinding.emailEditText.text.toString()
        val password: String = dataBinding.passwordEditText.text.toString()
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
            dataBinding.userNameEditText.error = getString(R.string.validUserName)
            dataBinding.userNameEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.userNameEditText.error = null
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            dataBinding.emailEditText.error = getString(R.string.validEmail)
            dataBinding.emailEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.emailEditText.error = null
        }
        if (password.isEmpty() || password.length < 6 || password.length > 12) {
            dataBinding.passwordEditText.error = getString(R.string.validPassword)
            dataBinding.passwordEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.passwordEditText.error = null
        }
        return valid
    }

    private fun checkPassLength() {
        dataBinding.passwordEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
    }

    private fun signup(userName: String, email: String, password: String) {
        dataBinding.animationLoading.visibility = View.VISIBLE
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
                            iSessionManagement.createLoginSession(
                                true, FirebaseAuth.getInstance().uid!!,
                                userName, "", "", email, "",
                                "", "", "", "0"
                            )
                            val intent = Intent(this, AboutYouActivity::class.java)
                            intent.putExtra("name", userName)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            showToastError(this@SignUpActivity, e.message.toString())
                        }
                } else {
                    // error
                    dataBinding.animationLoading.visibility = View.GONE
                    showToastError(this, it.exception?.message.toString())
                }
            }
    }
}