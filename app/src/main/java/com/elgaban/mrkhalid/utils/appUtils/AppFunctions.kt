package com.elgaban.mrkhalid.utils.appUtils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.elgaban.mrkhalid.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class AppFunctions {

    object Constants {

        fun isValidEmail(email: String?): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email.toString()).matches()
        }

        fun isValidPassword(password: String?): Boolean {
            return Pattern.compile("[a-zA-Z0-9!@#$-.]{8,24}").matcher(password.toString()).matches()
        }

        fun getToday(): String {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return df.format(c)
        }

        fun convertDateFormat(date_string: String?): String? {
            var date: Date? = null
            val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.US)
            try {
                date = formatter.parse(date_string.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return SimpleDateFormat("MM-dd-yyyy", Locale.US).format(date.toString())
        }

        fun showToastNoInternet(context: Activity) {
            MyMotionToast.darkToast(
                context, context.getString(R.string.connectedToInternet),
                MyMotionToast.TOAST_NO_INTERNET, MyMotionToast.TOAST_NO_INTERNET,
                MyMotionToast.GRAVITY_BOTTOM, MyMotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context, R.font.helvetica_regular)
            )
        }

        fun showToastError(context: Activity, message: String) {
            MyMotionToast.darkToast(
                context, message,
                MyMotionToast.TOAST_ERROR, MyMotionToast.TOAST_ERROR,
                MyMotionToast.GRAVITY_BOTTOM, MyMotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context, R.font.helvetica_regular)
            )
        }

        fun showToastSuccess(context: Activity, message: String) {
            MyMotionToast.darkToast(
                context, message,
                MyMotionToast.TOAST_SUCCESS, MyMotionToast.TOAST_SUCCESS,
                MyMotionToast.GRAVITY_BOTTOM, MyMotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context, R.font.helvetica_regular)
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

        fun getSpinnerIndex(spinner: Spinner, myString: String): Int {
            try {
                for (i in 0 until spinner.count) {
                    if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)
                    ) {
                        return i
                    }
                }
            } catch (e: Exception) {
            }
            // Check for this when you set the position.
            return -1
        }

        fun dpToPx(context: Context, valueInDp: Float): Float {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
        }

    }
}