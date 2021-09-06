package com.codetron.studentplanner.firebase

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codetron.studentplanner.R
import com.codetron.studentplanner.data.model.Task
import com.codetron.studentplanner.firebase.state.FirebaseTaskState
import com.codetron.studentplanner.utils.Utility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import javax.inject.Inject

private const val STUDENTS_REF = "students"
private const val TASKS_REF = "tasks"

class FirebaseTaskImpl @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    firebaseStorage: FirebaseStorage
) {

    private val taskCollectionRef = firebaseFirestore.collection(STUDENTS_REF)
    private val taskImageStorageRef = firebaseStorage.reference

    fun getTasksRemaining(): LiveData<FirebaseTaskState<List<Task>>> {
        val taskState: MutableLiveData<FirebaseTaskState<List<Task>>> =
            MutableLiveData(FirebaseTaskState.Loading())

        val listTask = arrayListOf<Task>()

        firebaseAuth.currentUser?.uid?.let { uid ->
            taskCollectionRef
                .document(uid)
                .collection(TASKS_REF)
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        value?.documents?.forEach { doc ->
                            val task = doc.toObject(Task::class.java)
                            task?.let {
                                if (Utility.toDate(it.date.toString()).after(Date()))
                                    listTask.add(it)
                            }
                        }
                        taskState.value = FirebaseTaskState.Success(listTask)
                    } else {
                        taskState.value = FirebaseTaskState.Error()
                    }
                }
        }
        return taskState
    }

    fun getAllTask(): LiveData<FirebaseTaskState<List<Task>>> {
        val taskState: MutableLiveData<FirebaseTaskState<List<Task>>> =
            MutableLiveData(FirebaseTaskState.Loading())

        val listTask = arrayListOf<Task>()

        firebaseAuth.currentUser?.uid?.let { uid ->
            taskCollectionRef
                .document(uid)
                .collection(TASKS_REF)
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        value?.documents?.forEach { doc ->
                            val task = doc.toObject(Task::class.java)
                            task?.let { listTask.add(it) }
                        }
                        taskState.value = FirebaseTaskState.Success(listTask)
                    } else {
                        taskState.value = FirebaseTaskState.Error()
                    }
                }
        }
        return taskState
    }

    fun getTask(taskId: String?): LiveData<FirebaseTaskState<Task>> {
        val taskState: MutableLiveData<FirebaseTaskState<Task>> =
            MutableLiveData(FirebaseTaskState.Loading())

        if (taskId != null) {
            firebaseAuth.currentUser?.uid?.let { uid ->
                taskCollectionRef
                    .document(uid)
                    .collection(TASKS_REF)
                    .document(taskId)
                    .get()
                    .addOnSuccessListener { doc ->
                        taskState.value =
                            FirebaseTaskState.Success(doc.toObject(Task::class.java))
                    }
                    .addOnFailureListener { taskState.value = FirebaseTaskState.Error() }
            }
        } else {
            taskState.value = FirebaseTaskState.Success()
        }

        return taskState
    }

    fun addTask(
        task: Task?
    ): LiveData<FirebaseTaskState<String>> {
        val taskState: MutableLiveData<FirebaseTaskState<String>> =
            MutableLiveData(FirebaseTaskState.Loading())

        if (task != null) {
            task.id?.let { taskId ->
                firebaseAuth.currentUser?.uid?.let { uid ->
                    addTask(uid, taskId, task, taskState)
                }
            }
        } else {
            taskState.value = FirebaseTaskState.Success()
        }

        return taskState
    }

    private fun addTask(
        uid: String,
        taskId: String,
        task: Task,
        taskState: MutableLiveData<FirebaseTaskState<String>>
    ) {
        taskCollectionRef
            .document(uid)
            .collection(TASKS_REF)
            .document(taskId)
            .set(task)
            .addOnSuccessListener {
                taskState.value =
                    FirebaseTaskState.Success(message = context.getString(R.string.success_add_task))
            }
            .addOnCanceledListener {
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_upload_image)}: Canceled")
            }
            .addOnFailureListener { e ->
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_add_task)} : ${e.message}")
            }
    }

    fun updateTask(
        task: Task?
    ): LiveData<FirebaseTaskState<String>> {
        val taskState: MutableLiveData<FirebaseTaskState<String>> =
            MutableLiveData(FirebaseTaskState.Loading())

        if (task != null) {
            task.id?.let { taskId ->
                firebaseAuth.currentUser?.uid?.let { uid ->
                    updateTask(uid, taskId, task, taskState)
                }
            }
        } else {
            taskState.value = FirebaseTaskState.Success()
        }

        return taskState
    }

    private fun updateTask(
        uid: String,
        taskId: String,
        task: Task,
        taskState: MutableLiveData<FirebaseTaskState<String>>
    ) {
        taskCollectionRef
            .document(uid)
            .collection(TASKS_REF)
            .document(taskId)
            .update(Task.toMapTask(task))
            .addOnSuccessListener {
                taskState.value =
                    FirebaseTaskState.Success(message = context.getString(R.string.success_update_task))
            }
            .addOnCanceledListener {
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_upload_image)}: Canceled")
            }
            .addOnFailureListener { e ->
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_update_task)} : ${e.message}")
            }
    }

    fun addUpdateTaskWithImage(
        task: Task?,
        isUpdate: Boolean,
    ): LiveData<FirebaseTaskState<String>> {

        val taskState: MutableLiveData<FirebaseTaskState<String>> =
            MutableLiveData(FirebaseTaskState.Loading())

        taskImageStorageRef
            .child("$STUDENTS_REF/${firebaseAuth.currentUser?.uid}/$TASKS_REF/image-${task?.id}")
            .putFile(Uri.parse(task?.image))
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    task?.image = uri.toString()
                    if (isUpdate) {
                        if (task != null) {
                            task.id?.let { taskId ->
                                firebaseAuth.currentUser?.uid?.let { uid ->
                                    updateTask(uid, taskId, task, taskState)
                                }
                            }
                        } else {
                            taskState.value =
                                FirebaseTaskState.Error(context.getString(R.string.error_update_task))
                        }
                    } else {
                        if (task != null) {
                            task.id?.let { taskId ->
                                firebaseAuth.currentUser?.uid?.let { uid ->
                                    addTask(uid, taskId, task, taskState)
                                }
                            }
                        } else {
                            taskState.value =
                                FirebaseTaskState.Error(context.getString(R.string.error_add_task))
                        }
                    }
                }?.addOnFailureListener { e ->
                    taskState.value =
                        FirebaseTaskState.Error("${context.getString(R.string.error_upload_image)}: ${e.message}")
                }
            }
            .addOnCanceledListener {
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_upload_image)}: Canceled")
            }
            .addOnFailureListener { e ->
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_upload_image)}: ${e.message}")
            }

        return taskState
    }

    fun removeTask(task: Task?): LiveData<FirebaseTaskState<String>> {
        val taskState: MutableLiveData<FirebaseTaskState<String>> =
            MutableLiveData(FirebaseTaskState.Loading())

        if (task != null) {
            firebaseAuth.currentUser?.uid?.let { uid ->
                task.id?.let { taskId ->
                    taskCollectionRef
                        .document(uid)
                        .collection(TASKS_REF)
                        .document(taskId)
                        .delete()
                        .addOnSuccessListener {
                            if (!task.image.equals("null")) {
                                removeImage(taskId, taskState)
                            } else {
                                taskState.value = FirebaseTaskState.Success(
                                    message = context.getString(R.string.success_delete_task)
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            taskState.value = FirebaseTaskState.Error(
                                "${context.getString(R.string.error_delete_task)}: ${e.message}"
                            )
                        }
                }

            }
        } else {
            taskState.value = FirebaseTaskState.Error()
        }

        return taskState
    }

    private fun removeImage(
        taskId: String,
        taskState: MutableLiveData<FirebaseTaskState<String>>
    ) {
        taskImageStorageRef
            .child("$STUDENTS_REF/${firebaseAuth.currentUser?.uid}/$TASKS_REF/image-$taskId")
            .delete()
            .addOnSuccessListener {
                taskState.value =
                    FirebaseTaskState.Success(message = context.getString(R.string.success_delete_task))
            }
            .addOnFailureListener { e ->
                taskState.value =
                    FirebaseTaskState.Error("${context.getString(R.string.error_delete_image)} : ${e.message.toString()}")
            }
    }

}