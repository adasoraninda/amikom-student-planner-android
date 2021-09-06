package com.codetron.studentplanner.firebase.state

import com.codetron.studentplanner.data.model.Info

sealed class FirebaseInfoState
object FirebaseInfoStateLoading : FirebaseInfoState()
object FirebaseInfoStateError : FirebaseInfoState()
data class FirebaseInfoStateSuccess(val data: List<Info>) : FirebaseInfoState()