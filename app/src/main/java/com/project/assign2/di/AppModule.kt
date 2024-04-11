// Package declaration indicating the location of the file within the project
package com.project.assign2.di

// Import statements for necessary classes and interfaces
import android.content.Context
import com.project.assign2.R
import com.project.assign2.data.network.datastore.DataStoreManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

// Dagger Module for providing dependencies
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provider function to provide default CoroutineDispatcher for IO operations
    @Provides
    @Singleton
    fun provideDefaultDispatcher() = Dispatchers.IO

    // Provider function to provide FirebaseAuth instance
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    // Provider function to provide GoogleSignInOptions
    @Provides
    @Singleton
    fun provideGso(@ApplicationContext context: Context) =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    // Provider function to provide GoogleSignInClient
    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context, gso: GoogleSignInOptions) =
        GoogleSignIn.getClient(context, gso)

    // Provider function to provide One Tap client
    @Provides
    @Singleton
    fun provideOneTapClient(@ApplicationContext context: Context) = Identity.getSignInClient(context)

    // Provider function to provide FirebaseFirestore instance
    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    // Provider function to provide DataStoreManager instance
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context) = DataStoreManager(context)
}
