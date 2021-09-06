package com.codetron.studentplanner.ui.auth.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codetron.studentplanner.R
import com.codetron.studentplanner.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonStartListener()
        buttonSignUpListener()
    }

    private fun buttonStartListener() {
        binding?.buttonStart?.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_fragment_to_sign_in_fragment)
        }
    }

    private fun buttonSignUpListener() {
        binding?.buttonSignUpHere?.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_fragment_to_sign_up_fragment)
        }
    }

}