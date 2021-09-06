package com.codetron.studentplanner.ui.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.data.validation.student.AccountAuthValidation
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import com.codetron.studentplanner.firebase.state.FirebaseAuthState

class SignInViewModel(
    private val firebaseUserImpl: FirebaseUserImpl,
    private val validation: AccountAuthValidation
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()

    private val _isEmailValid =
        Transformations.switchMap(_email) { validation.emailValidation(it) }
    val isEmailValid: LiveData<Boolean> get() = _isEmailValid

    private val _isPasswordValid =
        Transformations.switchMap(_password) { validation.passwordValidation(it) }
    val isPasswordValid: LiveData<Boolean> get() = _isPasswordValid

    private val _isAccountValid = MutableLiveData(false)

    private val _signInState = Transformations.switchMap(_isAccountValid) {
        if (it) {
            val email = _email.value
            val password = _password.value
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty())
                firebaseUserImpl.signInWithEmailAndPassword(email, password)
            else MutableLiveData(null)
        } else {
            MutableLiveData(null)
        }
    }
    val signInState: LiveData<FirebaseAuthState?> get() = _signInState

    private var _isPasswordVisible = MutableLiveData(false)
    val isPasswordVisible: LiveData<Boolean> get() = _isPasswordVisible

    fun togglePasswordVisible() {
        _isPasswordVisible.value = isPasswordVisible.value?.not()
    }

    fun setEmailPassword(email: String, password: String) {
        _email.value = email
        _password.value = password
        checkAccountValidation()
    }

    private fun checkAccountValidation() {
        _isAccountValid.value = validation.accountValidation(_email.value, _password.value)
    }

    fun setAccountValidFalse() {
        _isAccountValid.value = false
    }

}