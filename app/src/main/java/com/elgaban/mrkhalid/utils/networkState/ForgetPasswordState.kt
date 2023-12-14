package com.elgaban.mrkhalid.utils.networkState

data class ForgetPasswordState(
    val data: String? = null,
    val error: String = "",
    val isLoading: Boolean = false
)