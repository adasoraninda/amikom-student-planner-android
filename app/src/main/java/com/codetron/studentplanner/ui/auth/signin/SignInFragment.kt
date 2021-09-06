package com.codetron.studentplanner.ui.auth.signin

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
import com.codetron.studentplanner.databinding.FragmentSignInBinding
import com.codetron.studentplanner.dialog.LoadingIndicator
import com.codetron.studentplanner.firebase.state.FirebaseAuthStateError
import com.codetron.studentplanner.firebase.state.FirebaseAuthStateLoading
import com.codetron.studentplanner.firebase.state.FirebaseAuthStateSuccess
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var loadingIndicator: LoadingIndicator

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: SignInViewModel by viewModels { factory }

    private var toast: Toast? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as StudentPlannerApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSignUpListener()
        buttonSignInListener()
        buttonBackListener()
        buttonForgotPasswordListener()
        imagePasswordVisibilityListener()

        observeVisibilityPassword()
        observeEmailValidation()
        observePasswordValidation()
        observeSignInStudent()
    }

    private fun buttonBackListener() {
        binding?.viewBack?.setOnClickListener {
            hideKeyboard()
            findNavController().popBackStack()
        }
    }

    private fun buttonSignUpListener() {
        binding?.buttonSignUpHere?.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.action_sign_in_fragment_to_sign_up_fragment)
        }
    }

    private fun buttonSignInListener() {
        binding?.buttonSignIn?.setOnClickListener {
            signInUserValidation()
        }
    }

    private fun buttonForgotPasswordListener() {
        binding?.buttonForgotPassword?.setOnClickListener {
            makeToast(getString(R.string.not_available))
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

    private fun observeSignInStudent() {
        viewModel.signInState.observe(viewLifecycleOwner, {
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
                        findNavController().navigate(R.id.action_sign_in_fragment_to_home_activity)
                        viewModel.setAccountValidFalse()
                    }
                }
            }
        })
    }

    private fun signInUserValidation() {
        with(binding) {
            val email = this?.editTextEmail?.text?.trim().toString()
            val password = this?.editTextPassword?.text?.trim().toString()

            viewModel.setEmailPassword(email, password)
        }
    }

    private fun dismissLoadingIndicator() {
        loadingIndicator.dismiss()
    }

    private fun showLoadingIndicator() {
        loadingIndicator.show(
            requireActivity().supportFragmentManager,
            SignInFragment::class.java.simpleName
        )
    }

    private fun makeToast(message: String?) {
        toast = Toast.makeText(requireContext(), "$message", Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onDestroyView() {
        toast?.cancel()
        super.onDestroyView()
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding?.root?.windowToken, 0)
    }


}