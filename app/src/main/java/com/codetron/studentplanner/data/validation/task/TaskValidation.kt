package com.codetron.studentplanner.data.validation.task

import androidx.lifecycle.LiveData

interface TaskValidation {

    fun titleValidation(title: String?): LiveData<Boolean>

    fun descriptionValidation(description: String?): LiveData<Boolean>

    fun taskValidation(title: String?, description: String?): Boolean

}