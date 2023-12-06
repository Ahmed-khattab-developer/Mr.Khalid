package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.databinding.ActivitySignInBinding
import com.elgaban.mrkhalid.utils.appUtils.AppConstant.Constants.STUDENT
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thekhaeng.pushdownanim.PushDownAnim

class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivitySignInBinding
    private lateinit var iSessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        iSessionManagement = SessionManagement(this)
        checkPassLength()
        PushDownAnim.setPushDownAnimTo(dataBinding.signInButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.forgetPasswordTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.signUpTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v === dataBinding.forgetPasswordTextView) {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
        if (v === dataBinding.signUpTextView) {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        if (v === dataBinding.signInButton) {
            signInFunction()
        }
    }

    private fun signInFunction() {
        val emailS: String = dataBinding.emailEditText.text.toString()
        val passwordS = dataBinding.passwordEditText.text.toString()
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

    private fun login(email: String, password: String) {
        dataBinding.animationLoading.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseFirestore.getInstance().collection(STUDENT)
                        .document(it.result?.user?.uid!!).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    val student: Student? =
                                        document.toObject(Student::class.java)
                                    if (student?.profileCompleted == "0") {
                                        val intent = Intent(this, AboutYouActivity::class.java)
                                        intent.putExtra("name", student.name)
                                        intent.putExtra("email", student.email)
                                        startActivity(intent)
                                        finish()
                                    } else if (student?.profileCompleted == "1") {
                                        iSessionManagement.createLoginSession(
                                            true, student.id, student.name, student.phone,
                                            student.parentPhone, student.email, student.grade,
                                            student.birthDate, student.image, student.gender,
                                            student.profileCompleted
                                        )
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    startActivity(Intent(this, SignUpActivity::class.java))
                                    finish()
                                }
                            } else {
                                dataBinding.animationLoading.visibility = View.GONE
                                showToastError(this, it.exception.toString())
                            }
                        }
                } else {
                    // error
                    dataBinding.animationLoading.visibility = View.GONE
                    showToastError(this, getString(R.string.incorrectEmailOrPassword))
                }
            }
    }
}