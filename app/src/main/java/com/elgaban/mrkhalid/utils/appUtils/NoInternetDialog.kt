package com.elgaban.mrkhalid.utils.appUtils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.NoInternetDialogBinding

class NoInternetDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: NoInternetDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.no_internet_dialog, null, false
        )
        setContentView(binding.root)

    }

}