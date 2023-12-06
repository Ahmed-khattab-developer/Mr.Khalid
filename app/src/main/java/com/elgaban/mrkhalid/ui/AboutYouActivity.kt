package com.elgaban.mrkhalid.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.ActivityAboutYouBinding
import com.elgaban.mrkhalid.utils.appUtils.AppConstant.Constants.STUDENT
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.thekhaeng.pushdownanim.PushDownAnim

class AboutYouActivity : BaseActivity(), View.OnClickListener {

    private lateinit var dataBinding: ActivityAboutYouBinding
    private lateinit var iSessionManagement: SessionManagement

    private var mProfileUri: Uri? = null
    private var gradeList: MutableList<String>? = null
    private var grade: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_you)

        iSessionManagement = SessionManagement(this)

        checkPhoneNumberLength()

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
        gradeList?.add("الأول الثانوي")
        gradeList?.add("الثاني الثانوي")
        gradeList?.add("الثالث الثانوي")

        dataBinding.ageSpinner.item = gradeList as List<Any>?
        dataBinding.ageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    grade = gradeList!![position]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
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
        val gender: String
        val phone: String = dataBinding.phoneEditText.text.toString()
        val phoneParent: String = dataBinding.phoneparentEditText.text.toString()
        val getGender: String = dataBinding.genderCustomSwitch.checked.toString()
        val day: String? = dataBinding.dayDatePicker.seletedItem
        val month: String? = dataBinding.monthDatePicker.seletedItem
        val year: String? = dataBinding.yearDatePicker.seletedItem
        val dateOfBirth = "$day $month $year"
        gender = if (getGender == "LEFT") {
            "female"
        } else {
            "male"
        }
        if (!validate(phone, phoneParent, gender, dateOfBirth)) {
            return
        } else {
            hideKeyboard()
            if (Utils.hasInternetConnection(this)) {
                completeData(phone, phoneParent, gender, dateOfBirth)
            } else {
                showToastNoInternet(this)
            }
        }
    }

    private fun completeData(
        phone: String, phoneParent: String, gender: String, dateOfBirth: String
    ) {
        dataBinding.animationLoading.visibility = View.VISIBLE
        FirebaseStorage.getInstance().reference.child(STUDENT)
            .child(FirebaseAuth.getInstance().uid!!).putFile(mProfileUri!!)
            .addOnProgressListener {
                val progress = 100.0 * it.bytesTransferred / it.totalByteCount
                Log.e("progress", progress.toString())
            }.addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child(STUDENT)
                    .child(FirebaseAuth.getInstance().uid!!)
                    .downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl: String = uri.toString()
                        FirebaseFirestore.getInstance().collection(STUDENT)
                            .document(FirebaseAuth.getInstance().uid!!)
                            .update(
                                "phone", phone, "parentPhone", phoneParent,
                                "grade", grade, "birthDate", dateOfBirth, "image", imageUrl,
                                "gender", gender, "profileCompleted", "1"
                            )
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    iSessionManagement.createLoginSession(
                                        true, FirebaseAuth.getInstance().uid!!,
                                        intent.getStringExtra("name")!!, phone,
                                        phoneParent, intent.getStringExtra("email")!!, grade!!,
                                        dateOfBirth, imageUrl, gender, "1"
                                    )
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    // error
                                    dataBinding.animationLoading.visibility = View.GONE
                                    showToastError(this, it.exception?.message.toString())
                                }
                            }
                    }
            }.addOnFailureListener {
                dataBinding.animationLoading.visibility = View.GONE
                showToastError(this, it.message.toString())
            }
    }

    private fun validate(
        phone: String, phoneParent: String, gender: String, dateOfBirth: String
    ): Boolean {
        var valid = true
        if (phone.isEmpty() || phone.length < 11) {
            dataBinding.phoneEditText.error = getString(R.string.validPhone)
            dataBinding.phoneEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.phoneEditText.error = null
        }
        if (phoneParent.isEmpty() || phoneParent.length < 11) {
            dataBinding.phoneparentEditText.error = getString(R.string.validPhone)
            dataBinding.phoneparentEditText.isFocusable = true
            valid = false
        } else {
            dataBinding.phoneparentEditText.error = null
        }
        if (grade == null) {
            showToastError(this, getString(R.string.gradeNumber))
            valid = false
        }
        if (mProfileUri == null) {
            showToastError(this, getString(R.string.image))
            valid = false
        }
        if (gender.isEmpty()) {
            showToastError(this, getString(R.string.gender))
            valid = false
        }
        if (dateOfBirth == "") {
            showToastError(this, getString(R.string.birthDate))
            valid = false
        }
        return valid
    }

    private fun checkPhoneNumberLength() {
        dataBinding.phoneEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
        dataBinding.phoneparentEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
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
            showToastError(this, ImagePicker.getError(activityResult.data))
        }
    }

    private fun pickProfileImage() {
        ImagePicker.with(this)
            .crop()
            .cropFreeStyle()
            .maxResultSize(512, 512, true)
            .provider(ImageProvider.BOTH)
            .setDismissListener {
                Log.d("ImagePicker", "onDismiss")
            }
            .createIntentFromDialog { profileLauncher.launch(it) }
    }
}