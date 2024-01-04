package com.elgaban.mrkhalid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.elgaban.mrkhalid.R
import com.elgaban.mrkhalid.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private lateinit var dataBinding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        return dataBinding.root
    }
}