package com.codetron.studentplanner.data.validation.student

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class StudentAuthValidation @Inject constructor() : AccountAuthValidation {

    override fun nameValidation(name: String?): LiveData<Boolean> {
        return MutableLiveData(name.isNullOrBlank())
    }

    override fun emailValidation(email: String?): LiveData<Boolean> {
        return MutableLiveData(
            if (email.isNullOrBlank()) true
            else !Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
        )
    }

    override fun passwordValidation(password: String?): LiveData<Boolean> {
        return MutableLiveData(password?.length?.compareTo(5) ?: 0 < 1)
    }

    override fun accountValidation(
        email: String?,
        password: String?
    ): Boolean {
        return email.isNullOrEmpty().not()
            .and(password.isNullOrEmpty().not())
    }

    override fun accountValidation(
        name: String?,
        email: String?,
        password: String?
    ): Boolean {
        return name.isNullOrBlank().not()
            .and(email.isNullOrBlank().not())
            .and(password.isNullOrBlank().not())
    }
}