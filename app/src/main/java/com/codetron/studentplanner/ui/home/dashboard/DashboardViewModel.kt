package com.codetron.studentplanner.ui.home.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.data.model.Task
import com.codetron.studentplanner.firebase.FirebaseInfoImpl
import com.codetron.studentplanner.firebase.FirebaseTaskImpl
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import com.codetron.studentplanner.firebase.state.FirebaseInfoState
import com.codetron.studentplanner.firebase.state.FirebaseTaskState
import com.codetron.studentplanner.firebase.state.FirebaseUserState
import com.codetron.studentplanner.utils.Utility

class DashboardViewModel(
    firebaseUserImpl: FirebaseUserImpl,
    firebaseInfoImpl: FirebaseInfoImpl,
    firebaseTaskImpl: FirebaseTaskImpl
) : ViewModel() {

    val userData: LiveData<FirebaseUserState> = firebaseUserImpl.getUserData()

    val infoData: LiveData<FirebaseInfoState> = firebaseInfoImpl.getInfoBanner()

    val tasks: LiveData<FirebaseTaskState<List<Task>>> = firebaseTaskImpl.getTasksRemaining()

    val greetMessage: LiveData<String> = Utility.getGreetMessage()

}