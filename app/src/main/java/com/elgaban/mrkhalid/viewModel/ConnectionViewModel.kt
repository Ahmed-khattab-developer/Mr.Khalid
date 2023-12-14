package com.elgaban.mrkhalid.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.elgaban.mrkhalid.repository.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(private val connectivityRepository: ConnectivityRepository) : ViewModel() {

    val isOnline = connectivityRepository.isConnected.asLiveData()
}