package com.elgaban.mrkhalid.utils.networkState

import com.elgaban.mrkhalid.data.model.Student

data class UserState(
    val data: Student? = null,
    val error: String = "",
    val isLoading: Boolean = false
)