package com.codetron.studentplanner.ui.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import com.codetron.studentplanner.firebase.state.FirebaseUserState

class ProfileViewModel(private val firebaseUserImpl: FirebaseUserImpl) : ViewModel() {

    val userData: LiveData<FirebaseUserState> get() = firebaseUserImpl.getUserData()

    fun signOut() { firebaseUserImpl.userSignOut() }

}