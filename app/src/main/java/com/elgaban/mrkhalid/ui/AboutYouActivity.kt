package com.elgaban.mrkhalid.ui

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.hideKeyboard
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastError
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.customDatePicker.DatePicker
import com.elgaban.mrkhalid.utils.customSwitch.customSwitch
import com.elgaban.mrkhalid.utils.fontsmaterialuiux.cairoButton
import com.elgaban.mrkhalid.utils.fontsmaterialuiux.cairoEditText
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.thekhaeng.pushdownanim.PushDownAnim
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

class AboutYouActivity : AppCompatActivity(), View.OnClickListener {

    private var dayDatePicker: DatePicker? = null
    private var monthDatePicker: DatePicker? = null
    private var yearDatePicker: DatePicker? = null
    private var userNameEditText: cairoEditText? = null
    private var phoneEditText: cairoEditText? = null
    private var doneButton: cairoButton? = null
    private var imgProfile: AppCompatImageView? = null
    private var genderCustomSwitch: customSwitch? = null
    private var animationLoading: LottieAnimationView? = null

    private var file: File? = null

    private var mProfileUri: Uri? = null
    private var ageSpinner: SmartMaterialSpinner<String>? = null
    private var gradeList: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_you)

        /////*     initialize view   */////
        imgProfile = findViewById(R.id.imgProfile)
        userNameEditText = findViewById(R.id.id_userName_EditText)
        phoneEditText = findViewById(R.id.id_Phone_EditText)
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

        gradeList?.add("first")
        gradeList?.add("second")
        gradeList?.add("third")

        ageSpinner?.item = gradeList

        ageSpinner?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                Toast.makeText(this@AboutYouActivity, gradeList!![position], Toast.LENGTH_SHORT)
                    .show()
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
        var gender = ""
        val username: String = userNameEditText?.text.toString()
        val phone: String = phoneEditText?.text.toString()
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
        if (!validate(username, phone, gender, dateOfBirth)) {
            return
        } else {
            login(username, phone, gender, dateOfBirth)
        }
    }

    private fun login(username: String, phone: String, gender: String, dateOfBirth: String) {
        animationLoading?.visibility = View.VISIBLE
        Toast.makeText(this, "success$username $phone $gender $dateOfBirth", Toast.LENGTH_LONG)
            .show()
        hideKeyboard()
        if (Utils.hasInternetConnection(this)) {
//            lifecycleScope.launch {
//                val compressedImage = Compressor.compress(this@AboutYouActivity, file!!) {
//                    resolution(640, 320)
//                    quality(80)
//                    format(Bitmap.CompressFormat.JPEG)
//                    size(2_097_152) // 2 MB
//                }
//            }
        } else {
            showToastNoInternet(this)
        }
    }

    private fun validate(
        username: String, phone: String, gender: String, dateOfBirth: String
    ): Boolean {
        var valid = true
        if (username.isEmpty()) {
            userNameEditText?.error = getString(R.string.validusername)
            userNameEditText?.isFocusable = true
            valid = false
        } else {
            userNameEditText?.error = null
        }
        if (phone.isEmpty() || phone.length < 11) {
            phoneEditText?.error = getString(R.string.validPhone)
            phoneEditText?.isFocusable = true
            valid = false
        } else {
            phoneEditText?.error = null
        }
        if (mProfileUri == null) {
            showToastError(this, "أضف صورة شخصية")
            valid = false
        }
        if (gender.isEmpty()) {
            showToastError(this, "اختر النوع")
            valid = false
        }
        if (dateOfBirth == "") {
            showToastError(this, "اختر تاريخ الميلاد")
            valid = false
        }
        return valid
    }

    private fun checkPhoneNumberLength() {
        phoneEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                mProfileUri = uri
                imgProfile?.setImageURI(uri)
            } else {
                parseError(it)
            }
        }

    private fun parseError(activityResult: ActivityResult) {
        if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
            showToastError(this, ImagePicker.getError(activityResult.data))
        } else {
            showToastError(this, "Task Cancelled")
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