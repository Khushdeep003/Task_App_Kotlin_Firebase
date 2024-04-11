package com.project.assign2.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.project.assign2.R
import com.project.assign2.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    // Declaring NavController to manage navigation within the app
    private lateinit var navController: NavController

    // ViewModel instance for handling authentication-related operations
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieving reference to the BottomNavigationView defined in the layout
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        // Finding the NavHostFragment and obtaining its NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setting up the BottomNavigationView with NavController for navigation
        bottomNavigationView.setupWithNavController(navController)

        // Listening for destination changes in the NavController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // If the current destination is the sign-in or sign-up fragment
            if (destination.id == R.id.signInFragment || destination.id == R.id.signUpFragment) {
                // Check if the user is not authenticated
                if(!viewModel.userAuthenticatedStatus) {
                    // Navigate back to the todoFragment if not authenticated
                    navController.popBackStack(R.id.todoFragment, true)
                }
                // Hide the BottomNavigationView
                bottomNavigationView.visibility = View.GONE
            } else {
                // For other destinations, show the BottomNavigationView
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}
