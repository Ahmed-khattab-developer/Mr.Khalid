package com.elgaban.mrkhalid.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.databinding.ActivityMainBinding
import com.elgaban.mrkhalid.utils.appUtils.AppConstant
import com.elgaban.mrkhalid.utils.appUtils.AppFunctions.Constants.showToastNoInternet
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity
import com.elgaban.mrkhalid.utils.appUtils.Utils
import com.elgaban.mrkhalid.utils.userData.SessionManagement
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PROFILE_COMPLETED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private lateinit var iSessionManagement: SessionManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        iSessionManagement = SessionManagement(this)
        if (iSessionManagement.getUserDetails()[PROFILE_COMPLETED] == "1") {
            if (Utils.hasInternetConnection(this)) {
                dataBinding.animationLoading.visibility = View.VISIBLE
                FirebaseFirestore.getInstance().collection(AppConstant.Constants.STUDENT)
                    .document(FirebaseAuth.getInstance().uid!!).get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val document = it.result
                            if (document.exists()) {
                                val student: Student? =
                                    document.toObject(Student::class.java)
                                if (student?.profileCompleted == "1") {
                                    // not completed ask to complete
                                } else if (student?.profileCompleted == "2") {
                                    // completed and save to session
                                }
                                dataBinding.animationLoading.visibility = View.GONE
                            } else {
                                startActivity(Intent(this, SignUpActivity::class.java))
                                finish()
                            }
                        } else {
                            dataBinding.animationLoading.visibility = View.GONE
                        }
                    }
            } else {
                showToastNoInternet(this)
            }
        } else if (iSessionManagement.getUserDetails()[PROFILE_COMPLETED] == "2") {
            // completed
        }
    }
}