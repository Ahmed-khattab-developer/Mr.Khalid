package com.elgaban.mrkhalid.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.BIRTHDAY
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.DATASTORE_NAME
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.EMAIL
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.GENDER
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.GRADE
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.ID
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.IMAGE
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.IS_LOGIN
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.NAME
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.PARENT_PARENT
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.PASSWORD
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.PHONE
import com.elgaban.mrkhalid.repository.PreferenceDataStoreConstants.PROFILE_COMPLETED
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {

    override suspend fun getProfileCompleted(): String {
        val preference = context.dataStore.data.first()
        return preference[PROFILE_COMPLETED]!!
    }

    override suspend fun getName(): String {
        val preference = context.dataStore.data.first()
        return preference[NAME]!!
    }

    override suspend fun getEmail(): String {
        val preference = context.dataStore.data.first()
        return preference[EMAIL]!!
    }

    override suspend fun getPassword(): String {
        val preference = context.dataStore.data.first()
        return preference[PASSWORD]!!
    }

    override suspend fun getIsLoggedIn(): Boolean {
        return try {
            val preference = context.dataStore.data.first()
            preference[IS_LOGIN]!!
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun putProfileCompleted(value: String) {
        context.dataStore.edit {
            it[PROFILE_COMPLETED] = value
        }
    }

    override suspend fun putIsLoggedIn(value: Boolean) {
        context.dataStore.edit {
            it[IS_LOGIN] = value
        }
    }

    override suspend fun putStudentPreference(student: Student) {
        context.dataStore.edit { students ->
            students[ID] = student.id
            students[NAME] = student.name
            students[PHONE] = student.phone
            students[PARENT_PARENT] = student.parentPhone
            students[EMAIL] = student.email
            students[PASSWORD] = student.password
            students[GRADE] = student.grade
            students[BIRTHDAY] = student.birthDate
            students[IMAGE] = student.image
            students[GENDER] = student.gender
            students[PROFILE_COMPLETED] = student.profileCompleted
        }
    }

    override suspend fun getStudentPreference() {
        context.dataStore.data.map { student ->
            Student(
                id = student[ID]!!,
                name = student[NAME]!!,
                phone = student[PHONE]!!,
                parentPhone = student[PARENT_PARENT]!!,
                email = student[EMAIL]!!,
                password = student[PASSWORD]!!,
                grade = student[GRADE]!!,
                birthDate = student[BIRTHDAY]!!,
                image = student[IMAGE]!!,
                gender = student[GENDER]!!,
                profileCompleted = student[PROFILE_COMPLETED]!!
            )
        }
    }
}