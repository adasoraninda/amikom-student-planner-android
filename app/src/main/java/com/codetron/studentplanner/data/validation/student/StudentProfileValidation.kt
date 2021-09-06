package com.codetron.studentplanner.data.validation.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class StudentProfileValidation @Inject constructor() : AccountProfileValidation {

    override fun nameValidation(name: String?): LiveData<Boolean> {
        return MutableLiveData(name.isNullOrBlank())
    }

    override fun passwordValidation(password: String?): LiveData<Boolean> {
        return MutableLiveData(password?.length?.compareTo(5) ?: 0 < 1)
    }

    override fun accountValidation(
        name: String?,
        password: String?,
    ): Boolean {
        return name.isNullOrBlank().not()
            .and(password.isNullOrBlank().not())
    }
}