package com.elgaban.mrkhalid.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.elgaban.mrkhalid.data.model.Student
import com.elgaban.mrkhalid.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class AuthRepository
@Inject
constructor() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var loggedOutLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    private val storageFirebase = FirebaseStorage.getInstance()
    private val studentModel = "student"

    init {
        if (firebaseAuth.currentUser != null) {
            loggedOutLiveData.postValue(false)
        }
    }

    suspend fun register(email: String, password: String, student: Student) = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            student.id = firebaseAuth.currentUser!!.uid
            fireStoreDatabase.collection(studentModel)
                .document(firebaseAuth.currentUser!!.uid).set(student).await()
            emit((result.user?.let { Resource.Success(it) }!!))
            loggedOutLiveData.postValue(false)
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(
                Resource.Error(e.localizedMessage ?: "Check Your Internet Connection")
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: ""))
        }
    }

    suspend fun login(email: String, password: String) = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit((result.user?.let { Resource.Success(it) }!!))
            loggedOutLiveData.postValue(false)
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "خطأ في الايميل او الباسورد"))
        }

    }

    suspend fun forgetPassword(email: String) = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success("message send"))
            loggedOutLiveData.postValue(false)
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: ""))
        }
    }

    suspend fun getLoggedUser() = flow {
        emit(Resource.Loading())
        if (firebaseAuth.currentUser != null) {
            loggedOutLiveData.postValue(false)
            emit(Resource.Success(firebaseAuth.currentUser!!))
        } else {
            emit(Resource.Error("Not Logged"))
        }
    }

    suspend fun getUserData() = flow {
        emit(Resource.Loading())
        if (firebaseAuth.currentUser != null) {
            try {
                val snapshot = fireStoreDatabase.collection(studentModel)
                    .document(firebaseAuth.currentUser!!.uid).get().await()
                if (snapshot.exists()) {
                    val student: Student? = snapshot.toObject(Student::class.java)
                    emit(Resource.Success(student!!))
                }
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
            } catch (e: IOException) {
                emit(
                    Resource.Error(e.localizedMessage ?: "Check Your Internet Connection")
                )
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: ""))
            }
        }
    }

    suspend fun completeData(
        mProfileUri: Uri, phone: String, phoneParent: String,
        grade: String, gender: String, dateOfBirth: String
    ) = flow {
        emit(Resource.Loading())
        if (firebaseAuth.currentUser != null) {
            try {
                storageFirebase.reference.child(studentModel)
                    .child(firebaseAuth.currentUser!!.uid).putFile(mProfileUri).await()
                val imageUrl = storageFirebase.reference.child(studentModel)
                    .child(firebaseAuth.currentUser!!.uid).downloadUrl.await()
                fireStoreDatabase.collection(studentModel)
                    .document(firebaseAuth.currentUser!!.uid)
                    .update(
                        "phone", phone, "parentPhone", phoneParent,
                        "grade", grade, "birthDate", dateOfBirth, "image", imageUrl,
                        "gender", gender, "profileCompleted", "1"
                    ).await()
                emit(Resource.Success(imageUrl.toString()))
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
            } catch (e: IOException) {
                emit(
                    Resource.Error(e.localizedMessage ?: "Check Your Internet Connection")
                )
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: ""))
            }
        }
    }

    suspend fun logOut() {
        firebaseAuth.signOut()
        loggedOutLiveData.postValue(true)
    }

}