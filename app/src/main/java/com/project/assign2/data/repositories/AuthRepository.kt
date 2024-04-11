// Package declaration indicating the location of the file within the project
package com.project.assign2.data.repositories

// Import statements for necessary classes and interfaces
import com.project.assign2.data.network.datastore.DataStoreManager
import com.project.assign2.data.network.model.Response
import com.project.assign2.data.network.model.User
import com.project.assign2.util.Constants.Companion.TODOS
import com.project.assign2.util.Constants.Companion.USERS
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Repository class responsible for authentication-related operations
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth, // Firebase Authentication instance
    private val db: FirebaseFirestore, // Firebase Firestore instance
    private val dataStore: DataStoreManager, // DataStoreManager instance for managing data storage
    private val oneTapClient: SignInClient, // Google SignInClient instance
    private val defaultDispatcher: CoroutineDispatcher // Default CoroutineDispatcher for performing asynchronous operations
) {
    private var password = "" // Variable to store user's password
    private var authGoogle = false // Variable to store whether user authenticated with Google
    private var authEmail = false // Variable to store whether user authenticated with email

    // Boolean indicating whether a user is authenticated
    val userAuthenticatedStatus = auth.currentUser != null

    // Function to sign up a new user with email and password
    suspend fun signUpUser(email: String, password: String) =
        withContext(defaultDispatcher) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                addUserToFirestore()
                dataStore.savetoDataStore(password, authGoogle = false, authEmail = true)
                Response.Success(true)
            } catch (e: Exception) {
                Response.Error(e)
            }
        }

    // Function to sign in a user with email and password
    suspend fun signInUser(email: String, password: String) =
        withContext(defaultDispatcher) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                dataStore.savetoDataStore(password, authGoogle = false, authEmail = true)
                Response.Success(true)
            } catch (e: Exception) {
                Response.Error(e)
            }
        }

    // Function to sign in a user with Google account
    suspend fun signInWithGoogle(account: GoogleSignInAccount) =
        withContext(defaultDispatcher) {
            try {
                val task = auth.signInWithCredential(
                    GoogleAuthProvider.getCredential(account.idToken, null)
                ).await()
                val isNewUser = task.additionalUserInfo?.isNewUser
                if (isNewUser == true) {
                    addUserToFirestore()
                }
                dataStore.savetoDataStore(password = "", authGoogle = true, authEmail = false)
                Response.Success(true)
            } catch (e: Exception) {
                Response.Error(e)
            }
        }

    // Function to add user to Firestore database
    private suspend fun addUserToFirestore() {
        withContext(defaultDispatcher) {
            auth.currentUser?.apply {
                val user = User(uid, email)
                db.collection(USERS).document(user.userId).set(user).await()
            }
        }
    }

    // Function to sign out the user
    suspend fun signOutUser() =
        withContext(defaultDispatcher) {
            try {
                oneTapClient.signOut().await()
                firebaseAuthSignOut()
                Response.Success(true)
            } catch (e: Exception) {
                Response.Error(e)
            }
        }

    // Function to delete user account
    suspend fun deleteAccount() =
        withContext(defaultDispatcher) {
            try {
                // Retrieve user authentication preferences from DataStore
                dataStore.authPreference.first {
                    password = it.password
                    authEmail = it.authEmail
                    authGoogle = it.authGoogle
                    true
                }
                val currentUser = getUser()!!
                val userEmail = currentUser.email!!
                val userId = currentUser.uid

                // Perform sign-out based on authentication method
                if (authGoogle) {
                    oneTapClient.signOut().await()
                } else if (authEmail) {
                    val credential = EmailAuthProvider.getCredential(userEmail, password)
                    currentUser.reauthenticate(credential).await()
                }

                // Delete user account and associated data from Firestore
                currentUser.delete().await()
                val userTodos =
                    db.collection(TODOS).whereEqualTo("createdBy.userId", userId).get()
                        .await().documents
                for (todo in userTodos) {
                    db.collection(TODOS).document(todo.id).delete().await()
                }
                db.collection(USERS).document(userId).delete().await()
                firebaseAuthSignOut()
                Response.Success(true)
            } catch (e: Exception) {
                Response.Error(e)
            }
        }

    // Function to get current user
    suspend fun getUser() = withContext(defaultDispatcher) {
        auth.currentUser
    }

    // Function to sign out Firebase Authentication and update DataStore
    private suspend fun firebaseAuthSignOut() = withContext(defaultDispatcher) {
        auth.signOut()
        dataStore.savetoDataStore(password = "", authGoogle = false, authEmail = false)
    }
}
