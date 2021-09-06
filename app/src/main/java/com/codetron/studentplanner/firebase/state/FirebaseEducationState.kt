package com.codetron.studentplanner.firebase.state

import com.codetron.studentplanner.data.model.Education

sealed class FirebaseEducationState
data class FirebaseEducationStateSuccess(val data: Education) : FirebaseEducationState()
data class FirebaseEducationStateError(val message: String) : FirebaseEducationState()