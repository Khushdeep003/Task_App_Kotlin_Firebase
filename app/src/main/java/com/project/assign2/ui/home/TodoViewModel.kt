package com.project.assign2.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.assign2.data.network.model.Response
import com.project.assign2.data.network.model.Todo
import com.project.assign2.data.repositories.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    // MutableStateFlow to hold the list of todo items and their loading state
    private val _todos = MutableStateFlow<Response<List<Todo>>>(Response.Loading)

    // Expose todos as a StateFlow to observe changes in the todo list
    val todos = _todos.asStateFlow()

    init {
        // Coroutine launched in the viewModelScope to collect todo items from the repository
        viewModelScope.launch {
            todoRepository.getTodos().collect{ todos ->
                // Update the _todos StateFlow with the received response
                _todos.value = todos
            }
        }
    }

    // Function to insert a new todo item
    fun insertTodo(todoName: String) = viewModelScope.launch {
        // Launch a coroutine in the viewModelScope to call the insertTodo() function of the repository
        todoRepository.insertTodo(todoName)
    }

    // Function to update an existing todo item
    fun updateTodo(updatedTodo: Todo) = viewModelScope.launch {
        // Launch a coroutine in the viewModelScope to call the updateTodo() function of the repository
        todoRepository.updateTodo(updatedTodo)
    }

    // Function to delete a todo item
    fun deleteTodo(todoId: String) = viewModelScope.launch {
        // Launch a coroutine in the viewModelScope to call the deleteTodo() function of the repository
        todoRepository.deleteTodo(todoId)
    }
}
