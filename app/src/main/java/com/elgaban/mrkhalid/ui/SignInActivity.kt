package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.databinding.ActivitySignInBinding
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.NoInternetDialog
import com.elgaban.mrkhalid.viewModel.AuthViewModel
import com.elgaban.mrkhalid.viewModel.ConnectionViewModel
import com.elgaban.mrkhalid.viewModel.LoggedInViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivitySignInBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    private val connectionViewModel: ConnectionViewModel by viewModels()

    private var email: String? = null
    private var password: String? = null
    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        dataBinding.passwordEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
        PushDownAnim.setPushDownAnimTo(dataBinding.signInButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.forgetPasswordTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.signUpTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f)
            .setOnClickListener(this)

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
                authViewModel.student.collect {
                    if (it.isLoading) {
                        dataBinding.animationLoading.visibility = View.VISIBLE
                    }
                    if (it.error.isNotBlank()) {
                        dataBinding.animationLoading.visibility = View.GONE
                        showToastError(getString(R.string.incorrectEmailOrPassword))
                    }
                    it.data?.let {
                        authViewModel.getUserData()
                    }
                }
            }
        }

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
                        if (student.profileCompleted == "0") {

                            loggedInViewModel.putIsLoggedIn(true)
                            loggedInViewModel.saveStudentData(
                                Student(
                                    student.id, student.name, student.phone, student.parentPhone,
                                    student.email, student.password, student.grade,
                                    student.birthDate, student.image, student.gender, "0"
                                )
                            )

                            val intent = Intent(this@SignInActivity, AboutYouActivity::class.java)
                            intent.putExtra("name", student.name)
                            intent.putExtra("email", student.email)
                            intent.putExtra("password", student.password)
                            startActivity(intent)
                            finish()
                        } else if (student.profileCompleted == "1") {

                            loggedInViewModel.putIsLoggedIn(true)
                            loggedInViewModel.saveStudentData(
                                Student(
                                    student.id, student.name, student.phone, student.parentPhone,
                                    student.email, student.password, student.grade,
                                    student.birthDate, student.image, student.gender,
                                    "1"
                                )
                            )

                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        } else if (student.profileCompleted == "2") {

                            loggedInViewModel.putIsLoggedIn(true)
                            loggedInViewModel.saveStudentData(
                                Student(
                                    student.id, student.name, student.phone, student.parentPhone,
                                    student.email, student.password, student.grade,
                                    student.birthDate, student.image, student.gender,
                                    "1"
                                )
                            )

                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
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
        email = dataBinding.emailEditText.text.toString()
        password = dataBinding.passwordEditText.text.toString()
        if (!validate()) {
            return
        } else {
            hideKeyboard()
            authViewModel.login(email!!, password!!)
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (email?.isEmpty() == true || !Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            dataBinding.emailEditText.error = getString(R.string.validEmail)
            dataBinding.emailEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.emailEditText.error = null
        }
        if (password?.isEmpty() == true || password?.length!! < 6 || password?.length!! > 12) {
            dataBinding.passwordEditText.error = getString(R.string.validPassword)
            dataBinding.passwordEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.passwordEditText.error = null
        }
        return valid
    }
}