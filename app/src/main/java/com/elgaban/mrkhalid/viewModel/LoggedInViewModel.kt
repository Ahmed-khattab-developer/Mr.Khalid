package com.elgaban.mrkhalid.viewModel

import androidx.lifecycle.ViewModel
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.repository.DataStoreRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel @Inject constructor(
    private var dataStoreRepositoryImpl: DataStoreRepositoryImpl
) : ViewModel() {

    fun putProfileCompleted(value: String) = runBlocking {
        dataStoreRepositoryImpl.putProfileCompleted(value)
    }

    fun getProfileCompleted(): String = runBlocking {
        dataStoreRepositoryImpl.getProfileCompleted()
    }

    fun getName(): String = runBlocking {
        dataStoreRepositoryImpl.getName()
    }

    fun getEmail(): String = runBlocking {
        dataStoreRepositoryImpl.getEmail()
    }

    fun getPassword(): String = runBlocking {
        dataStoreRepositoryImpl.getPassword()
    }

    fun putIsLoggedIn(value: Boolean) = runBlocking {
        dataStoreRepositoryImpl.putIsLoggedIn(value)
    }

    fun getIsLoggedIn(): Boolean = runBlocking {
        dataStoreRepositoryImpl.getIsLoggedIn()
    }

    fun saveStudentData(student: Student) = runBlocking {
        dataStoreRepositoryImpl.putStudentPreference(student)
    }

    fun retrieveStudentData() = runBlocking {
        dataStoreRepositoryImpl.getStudentPreference()
    }

}