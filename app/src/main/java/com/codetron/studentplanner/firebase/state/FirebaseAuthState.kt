package com.codetron.studentplanner.firebase.state

sealed class FirebaseAuthState
object FirebaseAuthStateLoading : FirebaseAuthState()
data class FirebaseAuthStateSuccess(val message: String) : FirebaseAuthState()
data class FirebaseAuthStateError(val message: String) : FirebaseAuthState()