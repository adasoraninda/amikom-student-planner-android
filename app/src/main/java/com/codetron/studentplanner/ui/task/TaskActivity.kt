package com.codetron.studentplanner.ui.task

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.databinding.ActivityTaskBinding
import com.codetron.studentplanner.dialog.LoadingIndicator
import com.codetron.studentplanner.dialog.MyAlertDialog
import com.codetron.studentplanner.firebase.state.FirebaseTaskState
import com.codetron.studentplanner.ui.home.HomeActivity
import java.util.*
import javax.inject.Inject

private const val REQUEST_CODE_IMAGE_PICK = 0
private val TAG = TaskActivity::class.java.simpleName

class TaskActivity : AppCompatActivity() {

    private var _binding: ActivityTaskBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var loadingIndicator: LoadingIndicator

    @Inject
    lateinit var factory: TaskViewModel.Factory

    private val args: TaskActivityArgs by navArgs()

    private val viewModel: TaskViewModel by lazy {
        factory.create(args.taskId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as StudentPlannerApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        datePickerListener()
        navigateBackListener()
        buttonTaskListener()
        getImageListener()
        buttonDeleteListener()

        observeTitleToolbar()
        observeTitleValidation()
        observeDescriptionValidation()
        observeTask()
        observeAddUpdateTask()
        observeRemoveTask()
    }

    private fun navigateBackListener() {
        binding?.toolBar?.setBackButtonClickListener {
            val alertDialog = MyAlertDialog(
                getString(R.string.task),
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

    private fun datePickerListener() {
        binding?.imageButtonDate?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
            val calendarMonth = calendar.get(Calendar.MONTH)
            val calendarYear = calendar.get(Calendar.YEAR)

            val dateDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    binding?.textTaskDate?.text =
                        String.format(getString(R.string.format_date, dayOfMonth, month + 1, year))
                }, calendarYear, calendarMonth, calendarDay
            )

            calendar.time = Date()
            calendar.add(Calendar.DATE, 1)
            dateDialog.datePicker.minDate = calendar.time.time
            dateDialog.show()
        }
    }

    private fun getImageListener() {
        binding?.imageTask?.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                binding?.imageTask?.setImageURI(it)
                viewModel.setImageURI(it)
            }
        }
    }

    private fun buttonTaskListener() {
        binding?.buttonAddTask?.setOnClickListener { taskValidation() }
    }

    private fun buttonDeleteListener() {
        binding?.toolBar?.setActionClickListener {
            val alertDialog = MyAlertDialog(
                getString(R.string.task),
                String.format(
                    getString(R.string.remove_task),
                    binding?.editTextTaskTitle?.text?.toString()
                )
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss()
                    viewModel.setRemoveTaskTrue()
                } else dialog.dismiss()
            }
            alertDialog.show(supportFragmentManager, TAG)
        }
    }

    private fun taskValidation() {
        val title = binding?.editTextTaskTitle?.text?.trim().toString()
        val description = binding?.editTextTaskDesc?.text?.trim().toString()
        val date = binding?.textTaskDate?.text?.trim().toString()
        val priority = binding?.seekBarTaskPriority?.progress

        viewModel.setTask(title, description, date, priority)
    }

    private fun observeRemoveTask() {
        viewModel.removeTaskState.observe(this, { task ->
            if (task != null) {
                when (task) {
                    is FirebaseTaskState.Error -> {
                        if (task.message != null) {
                            dismissLoadingIndicator()
                            makeToast(task.message.toString())
                        }
                    }
                    is FirebaseTaskState.Loading -> showLoadingIndicator()
                    is FirebaseTaskState.Success -> {
                        viewModel.setRemoveTaskFalse()
                        dismissLoadingIndicator()
                        makeToast(task.message.toString())
                        HomeActivity.navigate(this)
                        finish()
                    }
                }
            }
        })
    }

    private fun observeAddUpdateTask() {
        viewModel.taskState.observe(this, { task ->
            when (task) {
                is FirebaseTaskState.Error -> {
                    if (task.message != null) {
                        dismissLoadingIndicator()
                        makeToast(task.message)
                    }
                }
                is FirebaseTaskState.Loading -> showLoadingIndicator()
                is FirebaseTaskState.Success -> {
                    dismissLoadingIndicator()
                    makeToast(task.message.toString())
                    HomeActivity.navigate(this)
                    finish()
                    viewModel.setTaskValidFalse()
                }
            }
        })
    }

    private fun observeTitleToolbar() {
        viewModel.isUpdateTask.observe(this, {
            if (it) {
                binding?.toolBar?.setTitle(getString(R.string.update_task))
                binding?.toolBar?.setAction(R.drawable.ic_delete)
                binding?.buttonAddTask?.text = getString(R.string.updating_task)
            } else {
                binding?.toolBar?.setTitle(getString(R.string.add_task))
                binding?.buttonAddTask?.text = getString(R.string.adding_task)
            }
        })
    }

    private fun observeTask() {
        viewModel.task.observe(this, { task ->
            when (task) {
                is FirebaseTaskState.Error -> {
                    binding?.progressBar?.visibility = View.GONE
                    makeToast("Error")
                }
                is FirebaseTaskState.Loading -> binding?.progressBar?.visibility = View.VISIBLE
                is FirebaseTaskState.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.task = task.data
                }
            }
        })
    }

    private fun observeTitleValidation() {
        viewModel.titleValidation.observe(this, {
            binding?.editTextTaskTitle?.error =
                if (it) getString(R.string.error_title)
                else null
        })
    }

    private fun observeDescriptionValidation() {
        viewModel.descriptionValidation.observe(this, {
            binding?.editTextTaskDesc?.error =
                if (it) getString(R.string.error_description)
                else null
        })
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun dismissLoadingIndicator() {
        loadingIndicator.dismiss()
    }

    private fun showLoadingIndicator() {
        loadingIndicator.show(supportFragmentManager, TAG)
    }

}