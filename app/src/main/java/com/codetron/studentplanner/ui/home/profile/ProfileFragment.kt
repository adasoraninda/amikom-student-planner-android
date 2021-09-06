package com.codetron.studentplanner.ui.home.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.databinding.FragmentProfileBinding
import com.codetron.studentplanner.dialog.MyAlertDialog
import com.codetron.studentplanner.firebase.state.FirebaseUserStateError
import com.codetron.studentplanner.firebase.state.FirebaseUserStateLoading
import com.codetron.studentplanner.firebase.state.FirebaseUserStateSuccess
import com.codetron.studentplanner.ui.auth.AuthActivity
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: ProfileViewModel by viewModels { factory }

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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemClickListener()

        observeUserData()
    }

    private fun observeUserData() {
        viewModel.userData.observe(viewLifecycleOwner, { user ->
            when (user) {
                is FirebaseUserStateError -> {
                    binding?.layoutProfileHeader?.progressBar?.visibility = View.GONE
                    makeToast(user.message)
                }
                FirebaseUserStateLoading -> binding?.layoutProfileHeader?.progressBar?.visibility =
                    View.VISIBLE
                is FirebaseUserStateSuccess -> {
                    binding?.layoutProfileHeader?.progressBar?.visibility = View.GONE
                    binding?.layoutProfileHeader?.student = user.data
                }
            }
        })
    }

    private fun itemClickListener() {
        with(binding?.layoutProfileBody) {
            this?.itemEditProfile?.setOnClickListener { findNavController().navigate(R.id.edit_profile_activity) }
            this?.itemLanguage?.setOnClickListener { makeToast(getString(R.string.not_available)) }
            this?.itemGiveRate?.setOnClickListener { makeToast(getString(R.string.not_available)) }
            this?.itemHelpCenter?.setOnClickListener { makeToast(getString(R.string.not_available)) }
            this?.itemLogout?.setOnClickListener { displayLogoutDialog() }
        }
    }

    private fun displayLogoutDialog() {
        val alertDialog = MyAlertDialog(
            context?.getString(R.string.logout),
            context?.getString(R.string.exit_message)
        ) { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                viewModel.signOut()
                navigateToAuth()
                dialog.dismiss()
            } else dialog.dismiss()
        }
        alertDialog.show(
            requireActivity().supportFragmentManager,
            ProfileFragment::class.java.simpleName
        )
    }

    private fun navigateToAuth() {
        AuthActivity.navigate(requireContext())
        requireActivity().finishAffinity()
    }

    private fun makeToast(message: String?) {
        toast = Toast.makeText(requireContext(), "$message", Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onDestroyView() {
        toast?.cancel()
        super.onDestroyView()
    }

}