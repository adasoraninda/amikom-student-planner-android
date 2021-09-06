package com.codetron.studentplanner.ui.home.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.adapter.TaskVerticalAdapter
import com.codetron.studentplanner.databinding.FragmentTasksBinding
import com.codetron.studentplanner.firebase.state.FirebaseTaskState
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var taskVerticalAdapter: TaskVerticalAdapter

    private val viewModel: TasksViewModel by viewModels { factory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as StudentPlannerApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTasks()

        addTaskButtonListener()

        observeTasks()
    }

    private fun initTasks() {
        binding?.tasks?.apply {
            adapter = taskVerticalAdapter.apply {
                setOnClickListener { id ->
                    val action = TasksFragmentDirections.actionMenuTaskToTaskActivity(id)
                    findNavController().navigate(action)
                }
            }
            setHasFixedSize(true)
        }
    }

    private fun addTaskButtonListener() {
        binding?.buttonAdd?.setOnClickListener {
            findNavController().navigate(R.id.action_menu_task_to_task_activity)
        }
    }

    private fun observeTasks() {
        viewModel.tasks.observe(viewLifecycleOwner, { task ->
            when (task) {
                is FirebaseTaskState.Error -> {
                    binding?.layoutEmptyTask?.root?.visibility = View.VISIBLE
                    binding?.progressBar?.visibility = View.GONE
                }
                is FirebaseTaskState.Loading -> {
                    binding?.layoutEmptyTask?.root?.visibility = View.GONE
                    binding?.progressBar?.visibility = View.VISIBLE
                    taskVerticalAdapter.submitList(emptyList())
                }
                is FirebaseTaskState.Success -> {
                    binding?.layoutEmptyTask?.root?.visibility = View.GONE
                    binding?.progressBar?.visibility = View.GONE
                    if (task.data.isNullOrEmpty()) {
                        binding?.layoutEmptyTask?.root?.visibility = View.VISIBLE
                    } else {
                        taskVerticalAdapter.submitList(task.data)
                    }
                }
            }
        })
    }

}