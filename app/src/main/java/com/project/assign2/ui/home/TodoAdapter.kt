// Package declaration indicating the location of the file within the project
package com.project.assign2.ui.home

// Import statements for necessary classes and interfaces
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.assign2.data.network.model.Todo
import com.project.assign2.databinding.TodoViewBinding

// Adapter class for displaying todo items in a RecyclerView
class TodoAdapter(private val listener: TodoEvents): // Listener for handling todo events
    ListAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback) {

    // Companion object for holding DiffUtil callback
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }
        }
    }

    // ViewHolder class for holding todo item views
    inner class TodoViewHolder(private val binding: TodoViewBinding) : RecyclerView.ViewHolder(binding.root) {

        private var currTodo: Todo? = null // Current todo item

        init{
            binding.apply{
                // Click listener for updating todo item status
                chkIsDone.setOnClickListener {
                    currTodo?.let { todo ->
                        if (chkIsDone.isChecked) {
                            listener.onTodoUpdate(
                                Todo(id = todo.id, name = todo.name, done = true)
                            )
                        } else {
                            listener.onTodoUpdate(
                                Todo(id = todo.id, name = todo.name, done = false)
                            )
                        }
                    }

                }
                // Long click listener for showing dialog for todo item options
                root.rootView.setOnLongClickListener {
                    currTodo?.let { todo ->
                        listener.callTodoDialog(todo)
                    }
                    true
                }
            }
        }

        // Function to bind todo item data to views
        fun bind(todo: Todo){
            currTodo = todo
            binding.apply {
                tvTodo.text = todo.name
                chkIsDone.isChecked = todo.done
            }
        }
    }

    // Function to create view holder for todo items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TodoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    // Function to bind todo item data to view holder
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currTodo = getItem(position)
        holder.bind(currTodo)
    }
}

// Interface for handling todo events
interface TodoEvents{
    fun onTodoUpdate(todo: Todo) // Function to handle todo update
    fun callTodoDialog(todo: Todo) // Function to call dialog for todo options
}
