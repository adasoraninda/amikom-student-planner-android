package com.codetron.studentplanner.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codetron.studentplanner.data.model.Student
import com.codetron.studentplanner.data.validation.student.AccountProfileValidation
import com.codetron.studentplanner.firebase.FirebaseEducationImpl
import com.codetron.studentplanner.firebase.FirebaseUserImpl
import com.codetron.studentplanner.firebase.state.FirebaseEducationState
import com.codetron.studentplanner.firebase.state.FirebaseProfileState
import com.codetron.studentplanner.firebase.state.FirebaseUserState
import com.codetron.studentplanner.firebase.state.FirebaseUserStateSuccess

class EditProfileViewModel(
    private val firebaseUserImpl: FirebaseUserImpl,
    private val firebaseEducationImpl: FirebaseEducationImpl,
    private val validation: AccountProfileValidation
) : ViewModel() {

    private val _name = MutableLiveData<String>()
    private val _email = MutableLiveData<String>()
    private val _education = MutableLiveData<String>()
    private val _grade = MutableLiveData<Int>()
    private val _password = MutableLiveData<String>()
    private val _photoUri = MutableLiveData<Uri>()

    val userData: LiveData<FirebaseUserState> = firebaseUserImpl.getUserData()

    val educationData: LiveData<FirebaseEducationState> = Transformations.switchMap(userData) {
        if (it is FirebaseUserStateSuccess) firebaseEducationImpl.getEducationData()
        else MutableLiveData(null)
    }

    private val _isNameValid =
        Transformations.switchMap(_name) { validation.nameValidation(it) }
    val isNameValid: LiveData<Boolean> get() = _isNameValid

    private val _isPasswordValid =
        Transformations.switchMap(_password) { validation.passwordValidation(it) }
    val isPasswordValid: LiveData<Boolean> get() = _isPasswordValid

    private var _isPasswordVisible = MutableLiveData(false)
    val isPasswordVisible: LiveData<Boolean> get() = _isPasswordVisible

    private val _isAccountValid = MutableLiveData(false)

    private val _editProfileState: MutableLiveData<FirebaseProfileState> =
        Transformations.switchMap(_isAccountValid) {
            if (it) {
                val student = _email.value?.let { email ->
                    Student.Builder(email)
                        .withName(_name.value.toString())
                        .withEducation(_education.value.toString())
                        .withGrade(_grade.value)
                        .withPhoto(getPhotoProfile())
                        .build()
                }
                if (_photoUri.value != null) firebaseUserImpl.updateUserDataWithPhoto(
                    student,
                    _password.value
                )
                else firebaseUserImpl.updateUserData(student, _password.value)
            } else MutableLiveData(null)
        } as MutableLiveData<FirebaseProfileState>

    val editProfileState: LiveData<FirebaseProfileState> get() = _editProfileState

    fun togglePasswordVisible() {
        _isPasswordVisible.value = isPasswordVisible.value?.not()
    }

    fun setEducation(education: String) {
        _education.value = education
    }

    fun setGrade(grade: Int) {
        _grade.value = grade
    }

    fun setAccount(name: String?, email: String?, password: String?) {
        _name.value = name
        _email.value = email
        _password.value = password
        checkAccountValidation()
    }

    fun setUserPhoto(uri: Uri) {
        _photoUri.value = uri
    }

    fun setAccountValidFalse() {
        _isAccountValid.value = false
    }

    private fun checkAccountValidation() {
        _isAccountValid.value =
            validation.accountValidation(_name.value, _password.value)
    }

    private fun getPhotoProfile(): String {
        return when {
            _photoUri.value.toString() != "null" -> _photoUri.value.toString()
            else -> {
                if (userData.value is FirebaseUserStateSuccess) {
                    if (!(userData.value as FirebaseUserStateSuccess).data.photo.equals("null"))
                        (userData.value as FirebaseUserStateSuccess).data.photo.toString()
                    else _photoUri.value.toString()
                } else _photoUri.value.toString()
            }
        }
    }

}