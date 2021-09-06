package com.codetron.studentplanner.data.validation.student

import androidx.lifecycle.LiveData

interface AccountProfileValidation {

    fun nameValidation(name: String?): LiveData<Boolean>

    fun passwordValidation(password: String?): LiveData<Boolean>

    fun accountValidation(
        name: String?,
        password: String?,
    ): Boolean

}