// Package declaration indicating the location of the file within the project
package com.project.assign2.ui.home

// Import statements for necessary classes and interfaces
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.assign2.data.network.model.Todo
import com.project.assign2.databinding.FragmentTodoAddBinding
import dagger.hilt.android.AndroidEntryPoint

// Fragment for adding a new todo item or editing an existing one
@AndroidEntryPoint
class TodoAddFragment : Fragment() {

    private var _binding: FragmentTodoAddBinding? = null
    private val binding get() = _binding!! // View binding instance
    private val viewModel: TodoViewModel by activityViewModels() // ViewModel instance shared with activity

    private val navigationArgs: TodoAddFragmentArgs by navArgs() // Arguments passed via navigation

    // Function to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Function called after the view is created, responsible for setting up UI components and event listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(title = navigationArgs.title) // Setting up toolbar title
        val todo = navigationArgs.todo // Retrieving todo item from arguments
        if(todo.id != ""){
            bind(todo) // Binding existing todo if editing
        } else {
            // Setting up save button click listener for adding new todo
            binding.btnSaveTodo.setOnClickListener {
                addNewTodo()
            }
        }
    }

    // Function to add a new todo item
    private fun addNewTodo(){
        binding.apply {
            if (txtEnterTodo.text.toString().isEmpty()) {
                txtEnterTodo.text = null
            } else {
                val todoName = txtEnterTodo.text.toString()
                viewModel.insertTodo(todoName) // Inserting new todo through ViewModel
                findNavController().navigateUp() // Navigating back to previous fragment
            }
        }
    }

    // Function to bind existing todo item data to views for editing
    private fun bind(todo: Todo) {
        binding.apply {
            txtEnterTodo.setText(todo.name) // Setting todo name in EditText
            // Setting up save button click listener for updating existing todo
            btnSaveTodo.setOnClickListener {
                viewModel.updateTodo(
                    Todo(
                        id = todo.id,
                        name = txtEnterTodo.text.toString(),
                        done = todo.done
                    )
                ) // Updating todo through ViewModel
                findNavController().navigateUp() // Navigating back to previous fragment
            }
        }
    }

    // Function to set toolbar title
    private fun setToolbar(title: String) {
        binding.todoAddFragmentToolbar.title = title
    }

    // Function called when the view is destroyed to clean up resources
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Releasing view binding instance
    }
}
