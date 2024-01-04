package com.elgaban.mrkhalid.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {

    private lateinit var dataBinding: FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_third, container, false)
        return dataBinding.root
    }

}