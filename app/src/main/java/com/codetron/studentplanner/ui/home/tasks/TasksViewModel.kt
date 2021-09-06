package com.codetron.studentplanner.ui.home.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.data.model.Task
import com.codetron.studentplanner.firebase.FirebaseTaskImpl
import com.codetron.studentplanner.firebase.state.FirebaseTaskState

class TasksViewModel(
    firebaseTaskImpl: FirebaseTaskImpl
) : ViewModel() {

    val tasks: LiveData<FirebaseTaskState<List<Task>>> = firebaseTaskImpl.getAllTask()

}