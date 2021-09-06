package com.codetron.studentplanner.firebase.state

sealed class FirebaseProfileState
object FirebaseProfileStateLoading : FirebaseProfileState()
data class FirebaseProfileStateSuccess(val message: String) : FirebaseProfileState()
data class FirebaseProfileStateError(val message: String) : FirebaseProfileState()