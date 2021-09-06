package com.codetron.studentplanner.firebase

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codetron.studentplanner.R
import com.codetron.studentplanner.data.model.Student
import com.codetron.studentplanner.firebase.state.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

private const val STUDENTS_REF = "students"
private const val PHOTO_REF = "photo-profile"

class FirebaseUserImpl @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    firebaseStorage: FirebaseStorage
) {
    private val studentCollectionRef = firebaseFirestore.collection(STUDENTS_REF)
    private val studentStorageRef = firebaseStorage.reference

    fun checkUserLogin(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun signInWithEmailAndPassword(email: String, password: String): LiveData<FirebaseAuthState> {
        val firebaseAuthState: MutableLiveData<FirebaseAuthState> =
            MutableLiveData(FirebaseAuthStateLoading)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firebaseAuthState.value = FirebaseAuthStateSuccess(
                    context.getString(R.string.success_sign_in)
                )
            }
            .addOnFailureListener { e ->
                firebaseAuthState.value = FirebaseAuthStateError(
                    "${context.getString(R.string.failed_sign_in)}: ${e.message.toString()}"
                )
            }

        return firebaseAuthState
    }

    fun createUserWithEmailAndPassword(
        student: Student,
        password: String?
    ): LiveData<FirebaseAuthState> {
        val firebaseAuthState: MutableLiveData<FirebaseAuthState> =
            MutableLiveData(FirebaseAuthStateLoading)

        if (password != null) {
            firebaseAuth
                .createUserWithEmailAndPassword(student.email, password)
                .addOnSuccessListener { result ->
                    result.user?.uid?.let { uid ->
                        storeUserToDatabase(uid, student, firebaseAuthState)
                    }
                }
                .addOnFailureListener { e ->
                    firebaseAuthState.value = FirebaseAuthStateError(
                        "${context.getString(R.string.failed_sign_up)}: ${e.message.toString()}"
                    )
                }
        } else {
            firebaseAuthState.value = FirebaseAuthStateError(
                "${context.getString(R.string.failed_sign_up)}: Error"
            )
        }

        return firebaseAuthState
    }

    private fun storeUserToDatabase(
        uid: String,
        data: Student,
        state: MutableLiveData<FirebaseAuthState>
    ) {
        studentCollectionRef
            .document(uid)
            .set(data)
            .addOnSuccessListener {
                state.value = FirebaseAuthStateSuccess(context.getString(R.string.success_sign_up))
            }.addOnFailureListener { e ->
                state.value =
                    FirebaseAuthStateError("${context.getString(R.string.failed_sign_up)}: ${e.message.toString()}")
            }
    }

    fun getUserData(): LiveData<FirebaseUserState> {
        val firebaseUserState: MutableLiveData<FirebaseUserState> =
            MutableLiveData(FirebaseUserStateLoading)

        firebaseAuth.currentUser?.uid?.let {
            studentCollectionRef
                .document(it)
                .get()
                .addOnSuccessListener { doc ->
                    firebaseUserState.value =
                        FirebaseUserStateSuccess(Student.fromMapStudent(doc.data))
                }
                .addOnFailureListener { e ->
                    firebaseUserState.value = FirebaseUserStateError(e.message.toString())
                }
        }
        return firebaseUserState
    }

    fun updateUserData(student: Student?, password: String?): LiveData<FirebaseProfileState> {
        val firebaseProfileState: MutableLiveData<FirebaseProfileState> =
            MutableLiveData(FirebaseProfileStateLoading)

        if (student != null) {
            firebaseAuth.currentUser?.uid?.let { uid ->
                if (password != null) {
                    updateUserPassword(password)?.continueWith {
                        updateUserData(student, uid, firebaseProfileState)
                    }
                } else {
                    firebaseProfileState.value = FirebaseProfileStateError(
                        context.getString(R.string.error_update_password)
                    )
                }
            }
        } else {
            firebaseProfileState.value = FirebaseProfileStateError(
                context.getString(R.string.error_update_profile)
            )
        }

        return firebaseProfileState
    }

    private fun updateUserData(
        student: Student,
        uid: String,
        firebaseProfileState: MutableLiveData<FirebaseProfileState>
    ) {
        studentCollectionRef
            .document(uid)
            .update(Student.toMapStudent(student))
            .addOnSuccessListener {
                firebaseProfileState.value =
                    FirebaseProfileStateSuccess(context.getString(R.string.success_update_profile))
            }
            .addOnFailureListener { e ->
                firebaseProfileState.value =
                    FirebaseProfileStateError("${context.getString(R.string.error_update_profile)} : ${e.message.toString()}")
            }
    }

    fun updateUserDataWithPhoto(
        student: Student?,
        password: String?
    ): LiveData<FirebaseProfileState> {
        val firebaseProfileState: MutableLiveData<FirebaseProfileState> =
            MutableLiveData(FirebaseProfileStateLoading)

        if (student != null) {
            studentStorageRef
                .child("$STUDENTS_REF/${firebaseAuth.currentUser?.uid}/$PHOTO_REF/photo-${student.email}")
                .putFile(Uri.parse(student.photo))
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        student.photo = uri.toString()
                        firebaseAuth.currentUser?.uid?.let { uid ->
                            if (password != null) {
                                updateUserPassword(password)?.continueWith {
                                    updateUserData(
                                        student,
                                        uid, firebaseProfileState
                                    )
                                }
                            } else {
                                firebaseProfileState.value = FirebaseProfileStateError(
                                    context.getString(R.string.error_update_password)
                                )
                            }
                        }
                    }?.addOnFailureListener { e ->
                        firebaseProfileState.value =
                            FirebaseProfileStateError("${context.getString(R.string.error_upload_image)} : ${e.message}")
                    }
                }
                .addOnCanceledListener {
                    firebaseProfileState.value =
                        FirebaseProfileStateError("${context.getString(R.string.error_upload_image)} : Canceled")
                }
                .addOnFailureListener { e ->
                    firebaseProfileState.value =
                        FirebaseProfileStateError("${context.getString(R.string.error_upload_image)}: ${e.message}")
                }
        }

        return firebaseProfileState
    }

    private fun updateUserPassword(password: String): Task<Void>? {
        return firebaseAuth
            .currentUser
            ?.updatePassword(password)
    }

    fun userSignOut() {
        firebaseAuth.signOut()
    }

}