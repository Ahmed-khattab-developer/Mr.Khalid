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
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.airbnb.lottie.LottieAnimationView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.appUtils.AppConstant.Constants.STUDENT
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.customDatePicker.DatePicker
import com.elgaban.mrkhalid.utils.customSwitch.customSwitch
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rishabhharit.roundedimageview.RoundedImageView
import com.thekhaeng.pushdownanim.PushDownAnim

class AboutYouActivity : BaseActivity(), View.OnClickListener {

    private var dayDatePicker: DatePicker? = null
    private var monthDatePicker: DatePicker? = null
    private var yearDatePicker: DatePicker? = null
    private var phoneEditText: AppCompatEditText? = null
    private var phoneParentEditText: AppCompatEditText? = null
    private var doneButton: AppCompatButton? = null
    private var imgProfile: RoundedImageView? = null
    private var genderCustomSwitch: customSwitch? = null
    private var animationLoading: LottieAnimationView? = null

    private var mProfileUri: Uri? = null
    private var ageSpinner: SmartMaterialSpinner<String>? = null
    private var gradeList: MutableList<String>? = null
    private var grade: String? = null

    private lateinit var iSessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_you)

        iSessionManagement = SessionManagement(this)

        /////*     initialize view   */////
        imgProfile = findViewById(R.id.imgProfile)
        phoneEditText = findViewById(R.id.id_Phone_EditText)
        phoneParentEditText = findViewById(R.id.id_Phone_parent_EditText)
        dayDatePicker = findViewById(R.id.id_day_DatePicker)
        monthDatePicker = findViewById(R.id.id_month_DatePicker)
        yearDatePicker = findViewById(R.id.id_year_DatePicker)
        genderCustomSwitch = findViewById(R.id.id_gender_customSwitch)
        doneButton = findViewById(R.id.id_done_Button)
        animationLoading = findViewById(R.id.animation_loading)
        ageSpinner = findViewById(R.id.age_spinner)
        checkPhoneNumberLength()

        /////*    add days , month and year         */////
        dayDatePicker!!.offset = 1
        dayDatePicker!!.setSeletion(14)
        dayDatePicker!!.setItems(
            ArrayList(resources.getStringArray(R.array.days).toMutableList())
        )

        monthDatePicker!!.offset = 1
        monthDatePicker!!.setSeletion(6)
        monthDatePicker!!.setItems(
            ArrayList(resources.getStringArray(R.array.months).toMutableList())
        )

        yearDatePicker!!.offset = 1
        yearDatePicker!!.setSeletion(100)
        yearDatePicker!!.setItems(
            ArrayList(resources.getStringArray(R.array.years).toMutableList())
        )

        /////*     On Click         */////
        PushDownAnim.setPushDownAnimTo(doneButton)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)
        PushDownAnim.setPushDownAnimTo(imgProfile)
            .setScale(PushDownAnim.MODE_SCALE, 0.85f).setOnClickListener(this)

        gradeList = ArrayList()

        gradeList?.add("الأول الثانوي")
        gradeList?.add("الثاني الثانوي")
        gradeList?.add("الثالث الثانوي")

        ageSpinner?.item = gradeList
        ageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                grade = gradeList!![position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    override fun onClick(v: View?) {
        if (v === doneButton) {
            complete()
        }
        if (v === imgProfile) {
            pickProfileImage()
        }
    }

    private fun complete() {
        /////*   Get  Email && Password    */////
        val gender: String
        val phone: String = phoneEditText?.text.toString()
        val phoneParent: String = phoneParentEditText?.text.toString()
        val getGender: String = genderCustomSwitch?.checked.toString()
        val day: String? = dayDatePicker?.seletedItem
        val month: String? = monthDatePicker?.seletedItem
        val year: String? = yearDatePicker?.seletedItem
        val dateOfBirth = "$day $month $year"
        gender = if (getGender == "LEFT") {
            "female"
        } else {
            "male"
        }

        /////*   Check if username ,gender and date of birth  are entered     */////
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
        animationLoading?.visibility = View.VISIBLE

        // Upload Task with upload to directory 'file'
        // and name of the file remains same
        FirebaseStorage.getInstance().reference.child(STUDENT)
            .child(FirebaseAuth.getInstance().uid!!).putFile(mProfileUri!!)
            .addOnProgressListener {
                val progress =
                    100.0 * it.bytesTransferred / it.totalByteCount
                Log.e("progress", progress.toString())
            }.addOnSuccessListener {
                // This listener is triggered when the file is uploaded successfully.
                // Using the below code you can get the download url of the file
                FirebaseStorage.getInstance().reference.child(STUDENT)
                    .child(FirebaseAuth.getInstance().uid!!)
                    .downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl: String = uri.toString()
                        Log.e("Firebase", "download passed")

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
                                    animationLoading?.visibility = View.GONE
                                    showToastError(this, it.exception?.message.toString())
                                }
                            }
                    }
            }.addOnFailureListener {
                Log.e("Firebase", "Failed in downloading")
            }
    }

    private fun validate(
        phone: String, phoneParent: String, gender: String, dateOfBirth: String
    ): Boolean {
        var valid = true
        if (phone.isEmpty() || phone.length < 11) {
            phoneEditText?.error = getString(R.string.validPhone)
            phoneEditText?.isFocusable = true
            valid = false
        } else {
            phoneEditText?.error = null
        }
        if (phoneParent.isEmpty() || phoneParent.length < 11) {
            phoneParentEditText?.error = getString(R.string.validPhone)
            phoneParentEditText?.isFocusable = true
            valid = false
        } else {
            phoneParentEditText?.error = null
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
        phoneEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
        phoneParentEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                mProfileUri = uri
                imgProfile?.setCornerRadius(20)
                imgProfile?.setPadding(0, 0, 0, 0)
                imgProfile?.setImageURI(uri)
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
            .provider(ImageProvider.BOTH) // Or bothCameraGallery()
            .setDismissListener {
                Log.d("ImagePicker", "onDismiss")
            }
            .createIntentFromDialog { profileLauncher.launch(it) }
    }
}