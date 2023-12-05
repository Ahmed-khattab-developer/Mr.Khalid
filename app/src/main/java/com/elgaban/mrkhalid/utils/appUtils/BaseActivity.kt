package com.elgaban.mrkhalid.utils.appUtils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val localeToSwitchTo = Locale("ar")
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

}