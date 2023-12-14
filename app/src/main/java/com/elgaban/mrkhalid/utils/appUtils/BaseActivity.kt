package com.elgaban.mrkhalid.utils.appUtils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.elgaban.mrkhalid.R
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val localeToSwitchTo = Locale("ar")
        val localeUpdatedContext: ContextWrapper =
            updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

    private fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
        var context = c
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else {
            configuration.locale = localeToSwitchTo
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context = context.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        return ContextWrapper(context)
    }

    fun showToastNoInternet() {
        MyMotionToast.darkToast(
            this, getString(R.string.connectedToInternet),
            MyMotionToast.TOAST_NO_INTERNET, MyMotionToast.TOAST_NO_INTERNET,
            MyMotionToast.GRAVITY_BOTTOM, MyMotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

    fun showToastError(message: String) {
        MyMotionToast.darkToast(
            this, message,
            MyMotionToast.TOAST_ERROR, MyMotionToast.TOAST_ERROR,
            MyMotionToast.GRAVITY_BOTTOM, MyMotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

    fun showToastSuccess(message: String) {
        MyMotionToast.darkToast(
            this, message,
            MyMotionToast.TOAST_SUCCESS, MyMotionToast.TOAST_SUCCESS,
            MyMotionToast.GRAVITY_BOTTOM, MyMotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )

    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}