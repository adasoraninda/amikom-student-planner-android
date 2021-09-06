package com.codetron.studentplanner.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codetron.studentplanner.data.model.Education
import com.codetron.studentplanner.firebase.state.FirebaseEducationState
import com.codetron.studentplanner.firebase.state.FirebaseEducationStateError
import com.codetron.studentplanner.firebase.state.FirebaseEducationStateSuccess
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

private const val EDUCATIONS_REF = "educations"
private const val EDUCATIONS_ID = "edu-mid"

class FirebaseEducationImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore
) {

    private val educationCollectionRef = firebaseFirestore.collection(EDUCATIONS_REF)

    fun getEducationData(): LiveData<FirebaseEducationState> {
        val firebaseEducationState = MutableLiveData<FirebaseEducationState>()

        educationCollectionRef
            .document(EDUCATIONS_ID)
            .get()
            .addOnSuccessListener { doc ->
                firebaseEducationState.value = doc.toObject(Education::class.java)?.let { edu ->
                    FirebaseEducationStateSuccess(edu)
                }
            }
            .addOnFailureListener { e ->
                firebaseEducationState.value = FirebaseEducationStateError(e.message.toString())
            }

        return firebaseEducationState
    }
}