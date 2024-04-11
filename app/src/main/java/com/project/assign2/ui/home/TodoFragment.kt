package com.project.assign2.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.assign2.R
import com.project.assign2.data.network.model.Todo
import com.project.assign2.data.network.model.Response
import com.project.assign2.databinding.FragmentTodoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Fragment responsible for displaying the list of todo items
@AndroidEntryPoint
class TodoFragment : Fragment(), TodoEvents {

    // View binding instance variable
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    // RecyclerView and its adapter variables
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var todoAdapter: TodoAdapter

    // ViewModel instance variable using by viewModels delegate
    private val viewModel: TodoViewModel by viewModels()

    // Function to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Function called after the view is created, responsible for setting up UI components and event listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up toolbar title
        setToolbar()

        // Setting up RecyclerView to display todo items
        setRecyclerView()

        // Observing changes in todo list data
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todos.collect { response ->
                    // Handling success response, updating UI accordingly
                    when (response) {
                        is Response.Success -> {
                            if (response.data.isEmpty()) {
                                binding.tvEmptyList.visibility = View.VISIBLE
                            } else {
                                todoAdapter.submitList(response.data)
                                binding.tvEmptyList.visibility = View.GONE
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        // Handling swipe gestures on todo items to delete them
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currTodo = todoAdapter.currentList[position]
                viewModel.deleteTodo(currTodo.id)
                Snackbar.make(view, "Article deleted successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        viewModel.insertTodo(currTodo.name)
                    }
                    show()
                }
            }
        }

        // Attaching ItemTouchHelper to RecyclerView
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(listRecyclerView)
        }

        // Navigating to add todo fragment when add button is clicked
        binding.btnAddTodo.setOnClickListener {
            val action = TodoFragmentDirections.actionTodoFragmentToTodoAddFragment(
                getString(R.string.add), Todo()
            )
            findNavController().navigate(action)
        }
    }


    // Function to display dialog for editing or deleting todo item
    private fun showTodoDialog(todo: Todo) {
        val options = arrayOf("Edit","Delete")
        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setItems(options){ _,which ->
                when(options[which]){
                    "Edit" -> onTodoEdit(todo)
                    "Delete" -> viewModel.deleteTodo(todo.id)
                }
            }
            .show()
    }

    // Function to set up RecyclerView with its adapter and layout manager
    private fun setRecyclerView(){
        todoAdapter = TodoAdapter(this)
        listRecyclerView = binding.listRecyclerView
        listRecyclerView.apply{
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    // Function to set toolbar title
    private fun setToolbar() {
        binding.todoFragmentToolbar.title = findNavController().currentDestination?.label
    }

    // Function called when todo item is updated
    private fun onTodoEdit(todo: Todo) {
        val action = TodoFragmentDirections.actionTodoFragmentToTodoAddFragment(
            getString(R.string.edit),todo
        )
        findNavController().navigate(action)
    }

    // Function called when todo item is updated
    override fun onTodoUpdate(todo: Todo) {
        viewModel.updateTodo(todo)
    }

    // Function called when dialog for todo item is requested
    override fun callTodoDialog(todo: Todo) {
        showTodoDialog(todo)
    }

    // Function called when the view is destroyed to clean up resources
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}