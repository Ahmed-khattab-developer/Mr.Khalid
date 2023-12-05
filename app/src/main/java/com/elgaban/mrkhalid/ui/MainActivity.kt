package com.elgaban.mrkhalid.ui

import android.os.Bundle
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.utils.appUtils.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}