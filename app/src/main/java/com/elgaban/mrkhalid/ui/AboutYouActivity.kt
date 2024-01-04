package com.elgaban.mrkhalid.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.databinding.ActivityAboutYouBinding
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.NoInternetDialog
import com.elgaban.mrkhalid.viewModel.AuthViewModel
import com.elgaban.mrkhalid.viewModel.ConnectionViewModel
import com.elgaban.mrkhalid.viewModel.LoggedInViewModel
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.firebase.auth.FirebaseAuth
import com.thekhaeng.pushdownanim.PushDownAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutYouActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivityAboutYouBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    private val connectionViewModel: ConnectionViewModel by viewModels()

    private var mProfileUri: Uri? = null
    private var gradeList: MutableList<String>? = null
    private var grade: String? = null
    private var gender: String? = null
    private var phone: String? = null
    private var phoneParent: String? = null
    private var dateOfBirth: String? = null
    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_you)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        dataBinding.phoneEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
        dataBinding.phoneparentEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))

        dataBinding.dayDatePicker.offset = 1
        dataBinding.dayDatePicker.setSeletion(14)
        dataBinding.dayDatePicker.setItems(
            ArrayList(resources.getStringArray(R.array.days).toMutableList())
        )
        dataBinding.monthDatePicker.offset = 1
        dataBinding.monthDatePicker.setSeletion(6)
        dataBinding.monthDatePicker.setItems(
            ArrayList(resources.getStringArray(R.array.months).toMutableList())
        )
        dataBinding.yearDatePicker.offset = 1
        dataBinding.yearDatePicker.setSeletion(100)
        dataBinding.yearDatePicker.setItems(
            ArrayList(resources.getStringArray(R.array.years).toMutableList())
        )

        PushDownAnim.setPushDownAnimTo(dataBinding.doneButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(dataBinding.imgProfile)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)

        gradeList = ArrayList()
        gradeList?.add(getString(R.string.first_grade))
        gradeList?.add(getString(R.string.second_grade))
        gradeList?.add(getString(R.string.third_grade))
        (gradeList as List<Any>?).also { dataBinding.ageSpinner.item = it }
        dataBinding.ageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    grade = gradeList!![position]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
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
                authViewModel.completeData.collect {
                    if (it.isLoading) {
                        dataBinding.animationLoading.visibility = View.VISIBLE
                    }
                    if (it.error.isNotBlank()) {
                        dataBinding.animationLoading.visibility = View.GONE
                        showToastError(it.error)
                    }
                    it.data?.let { imageUrl ->
                        dataBinding.animationLoading.visibility = View.GONE

                        loggedInViewModel.putIsLoggedIn(true)
                        loggedInViewModel.saveStudentData(
                            Student(
                                FirebaseAuth.getInstance().uid!!, intent.getStringExtra("name")!!,
                                phone!!, phoneParent!!, intent.getStringExtra("email")!!,
                                intent.getStringExtra("password")!!, grade!!, dateOfBirth!!,
                                imageUrl, gender!!, "1"
                            )
                        )
                        val intent = Intent(this@AboutYouActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v === dataBinding.doneButton) {
            complete()
        }
        if (v === dataBinding.imgProfile) {
            pickProfileImage()
        }
    }

    private fun complete() {
        phone = dataBinding.phoneEditText.text.toString()
        phoneParent = dataBinding.phoneparentEditText.text.toString()
        val getGender: String = dataBinding.genderCustomSwitch.checked.toString()
        val day: String? = dataBinding.dayDatePicker.seletedItem
        val month: String? = dataBinding.monthDatePicker.seletedItem
        val year: String? = dataBinding.yearDatePicker.seletedItem
        dateOfBirth = "$day $month $year"
        gender = if (getGender == "LEFT") {
            "بنت"
        } else {
            "ولد"
        }
        if (!validate()) {
            return
        } else {
            hideKeyboard()
            authViewModel.completeData(
                mProfileUri!!, phone!!, phoneParent!!, grade!!, gender!!, dateOfBirth!!
            )
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (phone?.isEmpty() == true || phone?.length!! < 11) {
            dataBinding.phoneEditText.error = getString(R.string.validPhone)
            dataBinding.phoneEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.phoneEditText.error = null
        }
        if (phoneParent?.isEmpty() == true || phoneParent?.length!! < 11) {
            dataBinding.phoneparentEditText.error = getString(R.string.validPhone)
            dataBinding.phoneparentEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.phoneparentEditText.error = null
        }
        if (grade == null) {
            showToastError(getString(R.string.gradeNumber))
            valid = false
        }
        if (mProfileUri == null) {
            showToastError(getString(R.string.image))
            valid = false
        }
        if (gender?.isEmpty() == true) {
            showToastError(getString(R.string.gender))
            valid = false
        }
        if (dateOfBirth == "") {
            showToastError(getString(R.string.birthDate))
            valid = false
        }
        return valid
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                mProfileUri = uri
                dataBinding.imgProfile.setCornerRadius(20)
                dataBinding.imgProfile.setPadding(0, 0, 0, 0)
                dataBinding.imgProfile.setImageURI(uri)
            } else {
                parseError(it)
            }
        }

    private fun parseError(activityResult: ActivityResult) {
        if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
            showToastError(ImagePicker.getError(activityResult.data))
        }
    }

    private fun pickProfileImage() {
        ImagePicker.with(this)
            .crop()
            .cropFreeStyle()
            .maxResultSize(512, 512, true)
            .provider(ImageProvider.BOTH)
            .setOutputFormat(Bitmap.CompressFormat.JPEG)
            .setDismissListener { Log.d("ImagePicker", "onDismiss") }
            .createIntentFromDialog { profileLauncher.launch(it) }
    }
}