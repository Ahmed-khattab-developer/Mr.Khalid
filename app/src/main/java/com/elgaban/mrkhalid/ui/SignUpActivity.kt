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
import com.elgaban.mrkhalid.databinding.ActivitySignUpBinding
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.NoInternetDialog
import com.elgaban.mrkhalid.viewModel.AuthViewModel
import com.elgaban.mrkhalid.viewModel.ConnectionViewModel
import com.elgaban.mrkhalid.viewModel.LoggedInViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    private val connectionViewModel: ConnectionViewModel by viewModels()

    private var userName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        dataBinding.passwordEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
        PushDownAnim.setPushDownAnimTo(dataBinding.SignInTextView)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.signUpButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)

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
                        showToastError(it.error)
                    }
                    it.data?.let { firebaseUser ->
                        loggedInViewModel.putIsLoggedIn(true)
                        loggedInViewModel.saveStudentData(
                            Student(
                                firebaseUser.uid, userName!!, "", "", email!!,
                                password!!, "", "", "", "0", ""
                            )
                        )
                        val intent = Intent(this@SignUpActivity, AboutYouActivity::class.java)
                        intent.putExtra("name", userName)
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
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
        userName = dataBinding.userNameEditText.text.toString()
        email = dataBinding.emailEditText.text.toString()
        password = dataBinding.passwordEditText.text.toString()
        if (!validate()) {
            return
        } else {
            hideKeyboard()
            val student = Student(
                "", userName!!, "", "", email!!, password!!,
                "", "", "", "", "0"
            )
            authViewModel.register(email!!, password!!, student)
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (userName?.isEmpty() == true) {
            dataBinding.userNameEditText.error = getString(R.string.validUserName)
            dataBinding.userNameEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.userNameEditText.error = null
        }
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