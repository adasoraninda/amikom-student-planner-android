package com.codetron.studentplanner.data.validation.student

import androidx.lifecycle.LiveData

interface AccountAuthValidation {

    fun nameValidation(name: String?): LiveData<Boolean>

    fun emailValidation(email: String?): LiveData<Boolean>

    fun passwordValidation(password: String?): LiveData<Boolean>

    fun accountValidation(
        email: String?,
        password: String?,
    ): Boolean

    fun accountValidation(
        name: String?,
        email: String?,
        password: String?,
    ): Boolean

}