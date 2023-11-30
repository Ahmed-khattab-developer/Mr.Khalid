package com.elgaban.mrkhalid.utils.userData

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.elgaban.mrkhalid.ui.Splash
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.IS_LOGIN
import com.elgaban.mrkhalid.utils.userData.SessionManagement.Constants.PREF_NAME
import java.util.*

@SuppressLint("CommitPrefEdits")
class SessionManagement constructor(context: Context?) {

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var _context: Context? = context

    // Shared preferences file name
    object Constants {
        const val PREF_NAME = "rx"
        const val IS_LOGIN = "IsLoggedIn"
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
        Status: Boolean?
    ) {
        editor!!.putBoolean(IS_LOGIN, Status!!)
        editor!!.commit()
    }

    fun getUserDetails(): HashMap<String, String?> {
        val users = HashMap<String, String?>()
        return users
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        editor!!.clear()
        editor!!.commit()
        val intent = Intent(_context, Splash::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        _context!!.startActivity(intent)
    }

    fun logout() {
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