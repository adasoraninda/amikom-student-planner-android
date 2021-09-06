package com.codetron.studentplanner.ui.task

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.data.model.Task
import com.codetron.studentplanner.data.validation.task.TaskValidation
import com.codetron.studentplanner.firebase.FirebaseTaskImpl
import com.codetron.studentplanner.firebase.state.FirebaseTaskState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskViewModel @AssistedInject constructor(
    @Assisted private val taskId: String?,
    private val firebaseTaskImpl: FirebaseTaskImpl,
    private val validation: TaskValidation
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(taskId: String?): TaskViewModel
    }

    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _date = MutableLiveData<String>()
    private val _priority = MutableLiveData<Int>()
    private val _imageURI = MutableLiveData<Uri>()

    val task = firebaseTaskImpl.getTask(taskId)

    val isUpdateTask: LiveData<Boolean> = Transformations.map(task) {
        if (it is FirebaseTaskState.Success) it.data != null else false
    }

    private val _titleValidation =
        Transformations.switchMap(_title) { validation.titleValidation(it) }
    val titleValidation: LiveData<Boolean> get() = _titleValidation

    private val _descriptionValidation =
        Transformations.switchMap(_description) { validation.descriptionValidation(it) }
    val descriptionValidation: LiveData<Boolean> get() = _descriptionValidation

    private var _isTaskValid = MutableLiveData<Boolean>()

    private val _taskState: MutableLiveData<FirebaseTaskState<String>> =
        Transformations.switchMap(_isTaskValid) {
            if (it) {
                val task = Task(
                    id = getTaskId(),
                    title = _title.value,
                    description = _description.value,
                    image = getTaskImage(),
                    date = _date.value,
                    priority = _priority.value,
                )
                if (isUpdateTask.value == true) {
                    if (_imageURI.value != null)
                        firebaseTaskImpl.addUpdateTaskWithImage(task, true)
                    else firebaseTaskImpl.updateTask(task)
                } else {
                    if (_imageURI.value != null)
                        firebaseTaskImpl.addUpdateTaskWithImage(task, false)
                    else firebaseTaskImpl.addTask(task)
                }
            } else {
                MutableLiveData(FirebaseTaskState.Error())
            }
        } as MutableLiveData<FirebaseTaskState<String>>

    val taskState: LiveData<FirebaseTaskState<String>> get() = _taskState

    private val _isRemoveTask = MutableLiveData<Boolean>()

    private val _removeTaskState: MutableLiveData<FirebaseTaskState<String>> =
        Transformations.switchMap(_isRemoveTask) {
            if (it) {
                val task = Task(
                    id = getTaskId(),
                    title = _title.value,
                    description = _description.value,
                    image = getTaskImage(),
                    date = _date.value,
                    priority = _priority.value,
                )
                firebaseTaskImpl.removeTask(task)
            } else {
                MutableLiveData(null)
            }
        } as MutableLiveData<FirebaseTaskState<String>>

    val removeTaskState: LiveData<FirebaseTaskState<String>> get() = _removeTaskState

    fun setTask(title: String?, description: String?, date: String?, priority: Int?) {
        _title.value = title
        _description.value = description
        _date.value = date
        _priority.value = priority
        checkTaskValidation()
    }

    fun setImageURI(uri: Uri?) {
        _imageURI.value = uri
    }

    fun setRemoveTaskTrue() {
        _isRemoveTask.value = true
    }

    fun setRemoveTaskFalse() {
        _isRemoveTask.value = false
    }

    fun setTaskValidFalse() {
        _isTaskValid.value = false
    }

    private fun checkTaskValidation() {
        _isTaskValid.value = validation.taskValidation(
            _title.value,
            _description.value,
        )
    }

    private fun getTaskImage(): String {
        return when {
            _imageURI.value.toString() != "null" -> _imageURI.value.toString()
            task.value?.data?.image?.equals("null") != true -> task.value?.data?.image.toString()
            else -> _imageURI.value.toString()
        }
    }

    private fun getTaskId(): String {
        return task.value?.data?.id ?: System.currentTimeMillis().toString()
    }

}