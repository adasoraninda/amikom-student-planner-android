package com.codetron.studentplanner.firebase.state

import com.codetron.studentplanner.data.model.Student

sealed class FirebaseUserState
object FirebaseUserStateLoading : FirebaseUserState()
data class FirebaseUserStateSuccess(val data: Student) : FirebaseUserState()
data class FirebaseUserStateError(val message: String) : FirebaseUserState()
