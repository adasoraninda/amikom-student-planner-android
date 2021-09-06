package com.codetron.studentplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codetron.studentplanner.data.model.Task
import com.codetron.studentplanner.databinding.ItemTaskHrBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskHorizontalAdapter @Inject constructor() :
    ListAdapter<Task, TaskHorizontalAdapter.TaskViewHolder>(DIFF_CALLBACK) {


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var clickListener: ((id: String?) -> Unit)? = null

    fun setOnClickListener(clickListener: ((id: String?) -> Unit)?) {
        this.clickListener = clickListener
    }

    class TaskViewHolder private constructor(private val binding: ItemTaskHrBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TaskViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTaskHrBinding.inflate(inflater, parent, false)
                return TaskViewHolder(binding)
            }
        }

        fun bind(task: Task?, clickListener: ((id: String?) -> Unit)?) {
            binding.task = task
            binding.root.setOnClickListener { clickListener?.invoke(task?.id) }
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.from(parent)


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}