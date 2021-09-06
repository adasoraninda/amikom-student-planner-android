package com.codetron.studentplanner.data.validation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class TaskValidationImpl @Inject constructor() : TaskValidation {

    override fun titleValidation(title: String?): LiveData<Boolean> {
        return MutableLiveData(title.isNullOrEmpty())
    }

    override fun descriptionValidation(description: String?): LiveData<Boolean> {
        return MutableLiveData(description.isNullOrEmpty())
    }

    override fun taskValidation(
        title: String?,
        description: String?,
    ): Boolean {
        return title.isNullOrBlank().not()
            .and(description.isNullOrBlank().not())
    }
}