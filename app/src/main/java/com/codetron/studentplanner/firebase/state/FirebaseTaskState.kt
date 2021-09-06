package com.codetron.studentplanner.firebase.state

sealed class FirebaseTaskState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null, message: String? = null) : FirebaseTaskState<T>(data, message)
    class Loading<T> : FirebaseTaskState<T>()
    class Error<T>(message: String? = null) : FirebaseTaskState<T>(message = message)
}