// Package declaration indicating the location of the file within the project
package com.project.assign2.ui.auth

// Import statements for necessary classes and interfaces
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.assign2.data.network.model.Response
import com.project.assign2.data.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel class for handling authentication related operations
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository // Repository for authentication operations
) : ViewModel() {

    // Property to observe user's authentication status
    val userAuthenticatedStatus get() = authRepository.userAuthenticatedStatus

    // SharedFlow for emitting responses related to email authentication
    private val _userAuthorizedEmail = MutableSharedFlow<Response<Boolean>>()
    val userAuthorizedEmail = _userAuthorizedEmail.asSharedFlow()

    // SharedFlow for emitting responses related to Google authentication
    private val _userAuthorizedGoogle = MutableSharedFlow<Response<Boolean>>()
    val userAuthorizedGoogle = _userAuthorizedGoogle.asSharedFlow()

    // Function to sign up a user with email and password
    fun signUpUser(email: String, password: String) = viewModelScope.launch {
        _userAuthorizedEmail.emit(Response.Loading)
        _userAuthorizedEmail.emit(authRepository.signUpUser(email, password))
    }

    // Function to sign in a user with email and password
    fun signInUser(email: String, password: String) = viewModelScope.launch {
        _userAuthorizedEmail.emit(Response.Loading)
        _userAuthorizedEmail.emit(authRepository.signInUser(email, password))
    }

    // Function to sign in a user with Google account
    fun signInWithGoogle(result: ActivityResult) = viewModelScope.launch {
        _userAuthorizedGoogle.emit(Response.Loading)
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            _userAuthorizedGoogle.emit(authRepository.signInWithGoogle(account))
        } catch (e: Exception) {
            _userAuthorizedGoogle.emit(Response.Error(e))
        }
    }
}
