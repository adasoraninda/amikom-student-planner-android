package com.codetron.studentplanner.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.data.model.Student
import com.codetron.studentplanner.data.validation.student.AccountAuthValidation
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import com.codetron.studentplanner.firebase.state.FirebaseAuthState
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val firebaseUserImpl: FirebaseUserImpl,
    private val validation: AccountAuthValidation
) : ViewModel() {

    private val _name = MutableLiveData<String>()
    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()

    private val _isNameValid =
        Transformations.switchMap(_name) { validation.nameValidation(it) }
    val isNameValid: LiveData<Boolean> get() = _isNameValid

    private val _isEmailValid =
        Transformations.switchMap(_email) { validation.emailValidation(it) }
    val isEmailValid: LiveData<Boolean> get() = _isEmailValid

    private val _isPasswordValid =
        Transformations.switchMap(_password) { validation.passwordValidation(it) }
    val isPasswordValid: LiveData<Boolean> get() = _isPasswordValid

    private val _isAccountValid = MutableLiveData(false)

    private val _signUpState = Transformations.switchMap(_isAccountValid) {
        if (it) {
            val student = _email.value?.let { email ->
                _name.value?.let { name ->
                    Student.Builder(email)
                        .withName(name)
                        .build()
                }
            }

            val password = _password
            if (student != null && password.value != null)
                firebaseUserImpl.createUserWithEmailAndPassword(student, password.value)
            else MutableLiveData(null)

        } else {
            MutableLiveData(null)
        }
    }
    val signUpState: LiveData<FirebaseAuthState?> get() = _signUpState

    private var _isPasswordVisible = MutableLiveData(false)
    val isPasswordVisible: LiveData<Boolean> get() = _isPasswordVisible

    fun togglePasswordVisible() {
        _isPasswordVisible.value = isPasswordVisible.value?.not()
    }

    fun setAccount(name: String, email: String, password: String) {
        _name.value = name
        _email.value = email
        _password.value = password
        checkAccountValidation()
    }

    private fun checkAccountValidation() {
        _isAccountValid.value =
            validation.accountValidation(_name.value, _email.value, _password.value)
    }

    fun setAccountValidFalse() {
        _isAccountValid.value = false
    }

}