package com.codetron.studentplanner.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codetron.studentplanner.data.model.Info
import com.codetron.studentplanner.firebase.state.FirebaseInfoState
import com.codetron.studentplanner.firebase.state.FirebaseInfoStateError
import com.codetron.studentplanner.firebase.state.FirebaseInfoStateLoading
import com.codetron.studentplanner.firebase.state.FirebaseInfoStateSuccess
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

private const val INFOS_REF = "infos"

class FirebaseInfoImpl @Inject constructor(
    firebaseStore: FirebaseFirestore
) {

    private val infoCollectionRef = firebaseStore.collection(INFOS_REF)

    fun getInfoBanner(): LiveData<FirebaseInfoState> {
        val infoState = MutableLiveData<FirebaseInfoState>(FirebaseInfoStateLoading)
        val listInfo = arrayListOf<Info>()

        infoCollectionRef
            .addSnapshotListener { value, error ->
                if (error?.message == null) {
                    value?.documents?.forEach { doc ->
                        val info = doc.toObject(Info::class.java)
                        info?.let { listInfo.add(it) }
                    }
                    infoState.value = FirebaseInfoStateSuccess(listInfo)
                } else {
                    infoState.value = FirebaseInfoStateError
                }
            }

        return infoState
    }

}