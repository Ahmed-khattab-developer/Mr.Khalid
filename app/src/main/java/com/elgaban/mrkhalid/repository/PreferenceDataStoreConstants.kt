package com.elgaban.mrkhalid.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    const val DATASTORE_NAME = "DATASTORE_NAME"
    val IS_LOGIN = booleanPreferencesKey("IsLoggedIn")
    val ID = stringPreferencesKey("id")
    val NAME = stringPreferencesKey("name")
    val PHONE = stringPreferencesKey("phone")
    val PARENT_PARENT = stringPreferencesKey("parentPhone")
    val EMAIL = stringPreferencesKey("email")
    val PASSWORD = stringPreferencesKey("password")
    val GRADE = stringPreferencesKey("grade")
    val BIRTHDAY = stringPreferencesKey("birthDate")
    val IMAGE = stringPreferencesKey("image")
    val GENDER = stringPreferencesKey("gender")
    val PROFILE_COMPLETED = stringPreferencesKey("profileCompleted")
}