package com.elgaban.mrkhalid.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.repository.AuthRepository
import com.elgaban.mrkhalid.utils.Resource
import com.elgaban.mrkhalid.utils.networkState.AuthState
import com.elgaban.mrkhalid.utils.networkState.CompleteDataState
import com.elgaban.mrkhalid.utils.networkState.ForgetPasswordState
import com.elgaban.mrkhalid.utils.networkState.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel
@Inject
constructor(private var authRepository: AuthRepository) : ViewModel() {

    private val _student = MutableStateFlow(AuthState())
    val student: StateFlow<AuthState> = _student

    private val _userData = MutableStateFlow(UserState())
    val userData: StateFlow<UserState> = _userData

    private val _forgetPass = MutableStateFlow(ForgetPasswordState())
    val forgetPass: StateFlow<ForgetPasswordState> = _forgetPass

    private val _completeData = MutableStateFlow(CompleteDataState())
    val completeData: StateFlow<CompleteDataState> = _completeData

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(email, password).onEach {
                when (it) {
                    is Resource.Loading -> {
                        _student.value = AuthState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _student.value = AuthState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _student.value = AuthState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun register(email: String, password: String, student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.register(email, password, student).onEach {
                when (it) {
                    is Resource.Loading -> {
                        _student.value = AuthState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _student.value = AuthState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _student.value = AuthState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun forgetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.forgetPassword(email).onEach {
                when (it) {
                    is Resource.Loading -> {
                        _forgetPass.value = ForgetPasswordState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _forgetPass.value = ForgetPasswordState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _forgetPass.value = ForgetPasswordState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun loggedUser() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getLoggedUser().onEach {
                when (it) {
                    is Resource.Loading -> {
                        _student.value = AuthState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _student.value = AuthState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _student.value = AuthState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getUserData().onEach {
                when (it) {
                    is Resource.Loading -> {
                        _userData.value = UserState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _userData.value = UserState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _userData.value = UserState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun completeData(
        mProfileUri: Uri, phone: String, phoneParent: String,
        grade: String, gender: String, dateOfBirth: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.completeData(
                mProfileUri, phone, phoneParent, grade, gender, dateOfBirth
            ).onEach {
                when (it) {
                    is Resource.Loading -> {
                        _completeData.value = CompleteDataState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _completeData.value = CompleteDataState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _completeData.value = CompleteDataState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}