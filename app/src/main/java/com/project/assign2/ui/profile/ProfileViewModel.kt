package com.project.assign2.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.assign2.data.network.model.Response
import com.project.assign2.data.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    // Flow representing the response of user profile operations (e.g., sign-out, delete account)
    private val _userProfile = MutableSharedFlow<Response<Boolean>>()
    val userProfile = _userProfile.asSharedFlow()

    // State flow representing the details of the currently authenticated user
    private val _userProfileDetails = MutableStateFlow<FirebaseUser?>(null)
    val userProfileDetails = _userProfileDetails.asStateFlow()

    // Initializes the ViewModel by fetching the user details upon creation
    init {
        getUser()
    }

    // Retrieves the user details and updates the state flow
    private fun getUser() = viewModelScope.launch {
        _userProfileDetails.value = authRepository.getUser()
    }

    // Signs out the user and emits the corresponding response
    fun signOutUser() = viewModelScope.launch {
        _userProfile.emit(Response.Loading)
        _userProfile.emit(authRepository.signOutUser())
    }

    // Deletes the user account and emits the corresponding response
    fun deleteUser() = viewModelScope.launch{
        _userProfile.emit(Response.Loading)
        _userProfile.emit(authRepository.deleteAccount())
    }
}
