package com.codetron.studentplanner.ui.home.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.adapter.TaskHorizontalAdapter
import com.codetron.studentplanner.data.model.Info
import com.codetron.studentplanner.databinding.FragmentDashboardBinding
import com.codetron.studentplanner.databinding.ItemInfoBannerBinding
import com.codetron.studentplanner.firebase.state.*
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var taskHorizontalAdapter: TaskHorizontalAdapter

    private val viewModel: DashboardViewModel by viewModels { factory }

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
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTasks()

        featureListener()
        buttonSeeAllTaskListener()

        observeUserData()
        observeInfoBanner()
        observeTasks()
        observeGreetMessage()

    }

    private fun initTasks() {
        binding?.layoutDashboardTasks?.tasks?.apply {
            adapter = taskHorizontalAdapter.apply {
                setOnClickListener { id ->
                    val action = DashboardFragmentDirections.actionMenuDashboardToTaskActivity(id)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun buttonSeeAllTaskListener() {
        binding?.layoutDashboardTasks?.textSeeAll?.setOnClickListener {
            makeToast(getString(R.string.not_available))
        }
    }

    private fun featureListener() {
        binding?.layoutDashboardFeature?.apply {
            featureTask.setOnClickListener { makeToast(getString(R.string.not_available)) }
            featureConsultation.setOnClickListener { makeToast(getString(R.string.not_available)) }
            featureSchedule.setOnClickListener { makeToast(getString(R.string.not_available)) }
        }
    }

    private fun observeGreetMessage() {
        viewModel.greetMessage.observe(viewLifecycleOwner, {
            binding?.layoutDashboardPhoto?.textGreetings?.text =
                String.format(getString(R.string.greet_message), it)
        })
    }

    private fun observeUserData() {
        viewModel.userData.observe(viewLifecycleOwner, { user ->
            binding?.layoutDashboardPhoto?.apply {
                when (user) {
                    is FirebaseUserStateError -> {
                        progressBar.visibility = View.GONE
                        makeToast(user.message)
                    }
                    FirebaseUserStateLoading -> progressBar.visibility = View.VISIBLE
                    is FirebaseUserStateSuccess -> {
                        progressBar.visibility = View.GONE
                        student = user.data
                    }
                }
            }

        })
    }

    private fun observeInfoBanner() {
        viewModel.infoData.observe(viewLifecycleOwner, { info ->
            binding?.layoutDashboardBanner?.apply {
                when (info) {
                    FirebaseInfoStateError -> {
                        progressBar.visibility = View.GONE
                    }
                    FirebaseInfoStateLoading -> progressBar.visibility = View.VISIBLE
                    is FirebaseInfoStateSuccess -> {
                        progressBar.visibility = View.GONE
                        initCarousel(info.data)
                    }
                }
            }
        })
    }

    private fun initCarousel(list: List<Info>) {
        binding?.layoutDashboardBanner?.apply {
            carouselInfo.setViewListener { position ->
                val infoView = ItemInfoBannerBinding.inflate(layoutInflater)
                infoView.info = list[position]
                infoView.root.setOnClickListener { makeToast(getString(R.string.not_available)) }
                infoView.root
            }
            carouselInfo.pageCount = list.size
        }
    }


    private fun observeTasks() {
        viewModel.tasks.observe(viewLifecycleOwner, { task ->
            when (task) {
                is FirebaseTaskState.Error -> {
                    binding?.layoutDashboardTasks?.layoutEmptyTask?.root?.visibility = View.VISIBLE
                    binding?.layoutDashboardTasks?.progressBar?.visibility = View.GONE
                }
                is FirebaseTaskState.Loading -> {
                    binding?.layoutDashboardTasks?.layoutEmptyTask?.root?.visibility = View.GONE
                    binding?.layoutDashboardTasks?.progressBar?.visibility = View.VISIBLE
                    taskHorizontalAdapter.submitList(emptyList())
                }
                is FirebaseTaskState.Success -> {
                    binding?.layoutDashboardTasks?.layoutEmptyTask?.root?.visibility = View.GONE
                    binding?.layoutDashboardTasks?.progressBar?.visibility = View.GONE
                    if (task.data.isNullOrEmpty()) {
                        binding?.layoutDashboardTasks?.layoutEmptyTask?.root?.visibility =
                            View.VISIBLE
                    } else {
                        taskHorizontalAdapter.submitList(task.data)
                    }
                }
            }
        })
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