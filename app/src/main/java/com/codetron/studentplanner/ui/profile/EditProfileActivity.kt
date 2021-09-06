package com.codetron.studentplanner.ui.profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.databinding.ActivityEditProfileBinding
import com.codetron.studentplanner.dialog.LoadingIndicator
import com.codetron.studentplanner.dialog.MyAlertDialog
import com.codetron.studentplanner.firebase.state.*
import com.codetron.studentplanner.ui.home.HomeActivity
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

private const val REQUEST_CODE_PHOTO_PICK = 0
private val TAG = EditProfileActivity::class.java.simpleName

class EditProfileActivity : AppCompatActivity() {

    private var _binding: ActivityEditProfileBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var loadingIndicator: LoadingIndicator

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: EditProfileViewModel by viewModels { factory }

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as StudentPlannerApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        navigateBackListener()
        buttonEditProfileListener()
        spinnerEducationItemSelectedListener()
        spinnerClassItemSelectedListener()
        imagePasswordVisibilityListener()
        getPhotoListener()

        observeUserData()
        observeUserEducation()
        observeNameValidation()
        observePasswordValidation()
        observeVisibilityPassword()
        observeEditProfile()
    }

    private fun initSpinnerEducation(educations: List<String>) {
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, educations)
        binding?.spinnerEducation?.adapter = arrayAdapter
        binding?.spinnerEducation?.setSelection(setSpinnerEducationSelection(educations))
    }

    private fun initSpinnerClass(grades: List<Int>) {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, grades)
        binding?.spinnerClass?.adapter = arrayAdapter
        binding?.spinnerClass?.setSelection(setSpinnerClassSelection(grades))
    }

    private fun setSpinnerClassSelection(list: List<Int>): Int {
        list.forEachIndexed { index, _ ->
            if (binding?.spinnerClass?.getItemAtPosition(index) == binding?.student?.grade) {
                return index
            }
        }
        return 0
    }

    private fun setSpinnerEducationSelection(list: List<String>): Int {
        list.forEachIndexed { index, _ ->
            if (binding?.spinnerEducation?.getItemAtPosition(index) == binding?.student?.education) {
                return index
            }
        }
        return 0
    }

    private fun getPhotoListener() {
        binding?.imageAddPhoto?.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_PHOTO_PICK)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO_PICK) {
            data?.data?.let {
                binding?.imagePhoto?.setImageURI(it)
                viewModel.setUserPhoto(it)
            }
        }
    }

    private fun navigateBackListener() {
        binding?.toolBar?.setBackButtonClickListener {
            val alertDialog = MyAlertDialog(
                getString(R.string.edit_profile),
                getString(R.string.exit_message)
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss()
                    finish()
                } else dialog.dismiss()
            }
            alertDialog.show(supportFragmentManager, TAG)
        }
    }

    private fun buttonEditProfileListener() {
        binding?.buttonEditProfile?.setOnClickListener {
            editProfileValidation()
        }
    }

    private fun imagePasswordVisibilityListener() {
        binding?.imagePasswordVisibility?.setOnClickListener {
            viewModel.togglePasswordVisible()
            binding?.editTextPassword?.setSelection(binding?.editTextPassword?.text?.length ?: 0)
        }
    }

    private fun spinnerEducationItemSelectedListener() {
        binding?.spinnerEducation?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setEducation(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun spinnerClassItemSelectedListener() {
        binding?.spinnerClass?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setGrade(parent?.getItemAtPosition(position).toString().toInt())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun observeUserData() {
        viewModel.userData.observe(this, { user ->
            when (user) {
                is FirebaseUserStateError -> {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.buttonEditProfile?.isEnabled = false
                    makeToast(user.message)
                }
                FirebaseUserStateLoading -> binding?.progressBar?.visibility = View.VISIBLE
                is FirebaseUserStateSuccess -> {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.student = user.data
                }
            }
        })
    }

    private fun observeUserEducation() {
        viewModel.educationData.observe(this, { education ->
            when (education) {
                is FirebaseEducationStateError -> makeToast(education.message)
                is FirebaseEducationStateSuccess -> {
                    education.data.education?.let { initSpinnerEducation(it) }
                    education.data.grade?.let { initSpinnerClass(it) }
                }
            }
        })
    }

    private fun observeVisibilityPassword() {
        viewModel.isPasswordVisible.observe(this, {
            if (it) {
                binding?.editTextPassword?.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding?.imagePasswordVisibility?.setImageDrawable(
                    binding?.imagePasswordVisibility?.context?.let { context ->
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_visibility_off
                        )
                    }
                )
            } else {
                binding?.editTextPassword?.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding?.imagePasswordVisibility?.setImageDrawable(
                    binding?.imagePasswordVisibility?.context?.let { context ->
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_visibility
                        )
                    }
                )
            }
        })
    }

    private fun observeNameValidation() {
        viewModel.isNameValid.observe(this, {
            binding?.editTextName?.error = if (it) getString(R.string.error_name) else null
        })
    }

    private fun observePasswordValidation() {
        viewModel.isPasswordValid.observe(this, {
            binding?.editTextPassword?.error = if (it) getString(R.string.error_password) else null
        })
    }

    private fun observeEditProfile() {
        viewModel.editProfileState.observe(this, { profile ->
            when (profile) {
                is FirebaseProfileStateError -> {
                    dismissLoadingIndicator()
                    makeToast(profile.message)
                }
                FirebaseProfileStateLoading -> showLoadingIndicator()
                is FirebaseProfileStateSuccess -> {
                    makeToast(profile.message)
                    dismissLoadingIndicator()
                    HomeActivity.navigate(this)
                    finish()
                    viewModel.setAccountValidFalse()
                }
            }
        })
    }

    private fun editProfileValidation() {
        val name = binding?.editTextName?.text?.trim().toString()
        val email = binding?.editTextEmail?.text?.trim().toString()
        val password = binding?.editTextPassword?.text?.trim().toString()

        viewModel.setAccount(name, email, password)
    }

    private fun dismissLoadingIndicator() {
        loadingIndicator.dismiss()
    }

    private fun showLoadingIndicator() {
        loadingIndicator.show(supportFragmentManager, TAG)
    }

    private fun makeToast(message: String?) {
        toast = Toast.makeText(this, "$message", Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onDestroy() {
        toast?.cancel()
        super.onDestroy()
    }

}