package com.elgaban.mrkhalid.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ConnectivityRepository @Inject constructor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected

    init {
        // Observe network connectivity changes

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    _isConnected.value = true
                }

                override fun onLost(network: android.net.Network) {
                    _isConnected.value = false
                }
            })
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)
            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)!! ->
                    _isConnected.value = true

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                    _isConnected.value = true

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->
                    _isConnected.value = true

                else -> _isConnected.value = false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                when (type) {
                    ConnectivityManager.TYPE_WIFI -> _isConnected.value = true
                    ConnectivityManager.TYPE_MOBILE -> _isConnected.value = true
                    ConnectivityManager.TYPE_ETHERNET -> _isConnected.value = true
                    else -> _isConnected.value = false
                }
            }
        }
    }
}