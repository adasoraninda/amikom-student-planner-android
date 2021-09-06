package com.codetron.studentplanner.ui.auth.signup

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.databinding.FragmentSignUpBinding
import com.codetron.studentplanner.dialog.LoadingIndicator
import com.codetron.studentplanner.firebase.state.FirebaseAuthStateError
import com.codetron.studentplanner.firebase.state.FirebaseAuthStateLoading
import com.codetron.studentplanner.firebase.state.FirebaseAuthStateSuccess
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding

    private var toast: Toast? = null

    @Inject
    lateinit var loadingIndicator: LoadingIndicator

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: SignUpViewModel by viewModels { factory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as StudentPlannerApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSignInListener()
        buttonSignUpListener()
        buttonBackListener()
        imagePasswordVisibilityListener()

        observeVisibilityPassword()
        observeNameValidation()
        observeEmailValidation()
        observePasswordValidation()
        observeCreateStudent()
    }

    private fun buttonBackListener() {
        binding?.viewBack?.setOnClickListener {
            hideKeyboard()
            findNavController().popBackStack()
        }
    }

    private fun buttonSignInListener() {
        binding?.buttonSignInHere?.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.action_sign_up_fragment_to_sign_in_fragment)
        }
    }

    private fun buttonSignUpListener() {
        binding?.buttonSignUp?.setOnClickListener {
            signUpUserValidation()
        }
    }

    private fun imagePasswordVisibilityListener() {
        binding?.imagePasswordVisibility?.setOnClickListener {
            viewModel.togglePasswordVisible()
            binding?.editTextPassword?.setSelection(binding?.editTextPassword?.text?.length ?: 0)
        }
    }

    private fun observeVisibilityPassword() {
        viewModel.isPasswordVisible.observe(viewLifecycleOwner, {
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
        viewModel.isNameValid.observe(viewLifecycleOwner, {
            binding?.editTextName?.error =
                if (it) getString(R.string.error_name)
                else null
        })
    }

    private fun observeEmailValidation() {
        viewModel.isEmailValid.observe(viewLifecycleOwner, {
            binding?.editTextEmail?.error =
                if (it) getString(R.string.error_email)
                else null
        })
    }

    private fun observePasswordValidation() {
        viewModel.isPasswordValid.observe(viewLifecycleOwner, {
            binding?.editTextPassword?.error =
                if (it) getString(R.string.error_password)
                else null
        })
    }

    private fun observeCreateStudent() {
        viewModel.signUpState.observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is FirebaseAuthStateError -> {
                        dismissLoadingIndicator()
                        makeToast(it.message)
                    }
                    FirebaseAuthStateLoading -> showLoadingIndicator()
                    is FirebaseAuthStateSuccess -> {
                        makeToast(it.message)
                        dismissLoadingIndicator()
                        findNavController().navigate(R.id.action_sign_up_fragment_to_home_activity)
                        viewModel.setAccountValidFalse()
                    }
                }
            }
        })
    }

    private fun signUpUserValidation() {
        with(binding) {
            val name = this?.editTextName?.text?.trim().toString()
            val email = this?.editTextEmail?.text?.trim().toString()
            val password = this?.editTextPassword?.text?.trim().toString()

            viewModel.setAccount(name, email, password)
        }
    }

    private fun dismissLoadingIndicator() {
        loadingIndicator.dismiss()
    }

    private fun showLoadingIndicator() {
        loadingIndicator.show(
            requireActivity().supportFragmentManager,
            SignUpFragment::class.java.simpleName
        )
    }

    private fun makeToast(message: String) {
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding?.root?.windowToken, 0)
    }


}