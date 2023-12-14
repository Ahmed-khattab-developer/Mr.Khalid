package com.elgaban.mrkhalid.repository

import com.elgaban.mrkhalid.data.model.Student

interface DataStoreRepository {
    suspend fun getProfileCompleted(): String
    suspend fun getName(): String
    suspend fun getEmail(): String
    suspend fun getPassword(): String
    suspend fun getIsLoggedIn(): Boolean
    suspend fun getStudentPreference()
    suspend fun putProfileCompleted(value: String)
    suspend fun putIsLoggedIn(value: Boolean)
    suspend fun putStudentPreference(student: Student)
}