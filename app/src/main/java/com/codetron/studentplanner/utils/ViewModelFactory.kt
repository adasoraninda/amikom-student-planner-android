package com.codetron.studentplanner.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codetron.studentplanner.data.validation.student.AccountAuthValidation
import com.codetron.studentplanner.data.validation.student.AccountProfileValidation
import com.codetron.studentplanner.firebase.FirebaseEducationImpl
import com.codetron.studentplanner.firebase.FirebaseInfoImpl
import com.codetron.studentplanner.firebase.FirebaseTaskImpl
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import com.codetron.studentplanner.ui.auth.signin.SignInViewModel
import com.codetron.studentplanner.ui.auth.signup.SignUpViewModel
import com.codetron.studentplanner.ui.home.dashboard.DashboardViewModel
import com.codetron.studentplanner.ui.home.profile.ProfileViewModel
import com.codetron.studentplanner.ui.home.tasks.TasksViewModel
import com.codetron.studentplanner.ui.profile.EditProfileViewModel
import com.codetron.studentplanner.ui.splash.SplashViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val firebaseUserImpl: FirebaseUserImpl,
    private val firebaseEducationImpl: FirebaseEducationImpl,
    private val firebaseInfoImpl: FirebaseInfoImpl,
    private val firebaseTaskImpl: FirebaseTaskImpl,
    private val accountAuthValidation: AccountAuthValidation,
    private val accountProfileValidation: AccountProfileValidation,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(firebaseUserImpl) as T
            }
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(firebaseUserImpl, accountAuthValidation) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(firebaseUserImpl, accountAuthValidation) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(firebaseUserImpl, firebaseInfoImpl, firebaseTaskImpl) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(firebaseUserImpl) as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(
                    firebaseUserImpl,
                    firebaseEducationImpl,
                    accountProfileValidation
                ) as T
            }
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> {
                TasksViewModel(firebaseTaskImpl) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown View Model Class: ${modelClass.name}")
            }
        }
    }
}