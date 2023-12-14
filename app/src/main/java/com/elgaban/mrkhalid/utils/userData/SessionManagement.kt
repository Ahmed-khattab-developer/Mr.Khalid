package com.elgaban.mrkhalid.utils.userData

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.elgaban.mrkhalid.ui.Splash
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.BIRTHDAY
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.EMAIL
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.GENDER
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.GRADE
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.ID
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.IMAGE
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.IS_LOGIN
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.NAME
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PARENT_PARENT
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PHONE
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PREF_NAME
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PROFILE_COMPLETED
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SessionManagement constructor(context: Context?) {

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var _context: Context? = context

    // Shared preferences file name
    object Constants {
        const val PREF_NAME = "rx"
        const val IS_LOGIN = "IsLoggedIn"
        const val ID = "id"
        const val NAME = "name"
        const val PHONE = "phone"
        const val PARENT_PARENT = "parentPhone"
        const val EMAIL = "email"
        const val GRADE = "grade"
        const val BIRTHDAY = "birthDate"
        const val IMAGE = "image"
        const val GENDER = "gender"
        const val PROFILE_COMPLETED = "profileCompleted"
    }

    init {
        val privateMode = 0
        pref = _context!!.getSharedPreferences(PREF_NAME, privateMode)
        editor = pref?.edit()
    }

    /**
     * Get stored session data
     */
    fun createLoginSession(
        status: Boolean?, id: String, name: String, phone: String,
        parentPhone: String, email: String, grade: String, birthDay: String,
        image: String, gender: String, profileCompleted: String
    ) {
        editor!!.putBoolean(IS_LOGIN, status!!)
        editor!!.putString(ID, id)
        editor!!.putString(NAME, name)
        editor!!.putString(PHONE, phone)
        editor!!.putString(PARENT_PARENT, parentPhone)
        editor!!.putString(EMAIL, email)
        editor!!.putString(GRADE, grade)
        editor!!.putString(BIRTHDAY, birthDay)
        editor!!.putString(IMAGE, image)
        editor!!.putString(GENDER, gender)
        editor!!.putString(PROFILE_COMPLETED, profileCompleted)
        editor!!.commit()
    }

    fun getUserDetails(): HashMap<String, String?> {
        val users = HashMap<String, String?>()
        users[ID] = pref!!.getString(ID, null)
        users[NAME] = pref!!.getString(NAME, null)
        users[PHONE] = pref!!.getString(PHONE, null)
        users[PARENT_PARENT] = pref!!.getString(PARENT_PARENT, null)
        users[EMAIL] = pref!!.getString(EMAIL, null)
        users[GRADE] = pref!!.getString(GRADE, null)
        users[BIRTHDAY] = pref!!.getString(BIRTHDAY, null)
        users[IMAGE] = pref!!.getString(IMAGE, null)
        users[GENDER] = pref!!.getString(GENDER, null)
        users[PROFILE_COMPLETED] = pref!!.getString(PROFILE_COMPLETED, null)
        return users
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        editor!!.clear()
        editor!!.commit()
        val intent = Intent(_context, Splash::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        _context!!.startActivity(intent)
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        editor!!.clear()
        editor!!.commit()
    }

    /**
     * Quick check for login
     */
    fun isLoggedIn(): Boolean {
        return pref!!.getBoolean(IS_LOGIN, false)
    }
}