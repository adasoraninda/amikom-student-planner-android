package com.codetron.studentplanner.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val firebaseUserImpl: FirebaseUserImpl
) : ViewModel() {

    private val _isNavigateToWelcome = MutableLiveData<Boolean>()
    val isNavigateWelcome: LiveData<Boolean>
        get() = _isNavigateToWelcome

    private val _isNavigateToHome = MutableLiveData<Boolean>()
    val isNavigateToHome: LiveData<Boolean>
        get() = _isNavigateToHome

    init {
        doNavigate()
    }

    private fun doNavigate() = viewModelScope.launch {
        delay(3000)
        if (firebaseUserImpl.checkUserLogin()) {
            _isNavigateToHome.value = true
        } else {
            _isNavigateToWelcome.value = true
        }
    }

    fun doneNavigate() {
        _isNavigateToWelcome.value = false
        _isNavigateToHome.value = false
    }

}