// Package declaration indicating the location of the file within the project
package com.project.assign2.ui.auth

// Import statements for necessary classes and interfaces
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.project.assign2.R
import com.project.assign2.databinding.FragmentSignInBinding
import com.project.assign2.data.network.model.Response
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

// Fragment for handling user sign-in functionality
@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels() // ViewModel instance for authentication

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient // GoogleSignInClient instance

    // Function to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Function to initialize views and set up click listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If user is already authenticated, navigate to todo fragment
        if (viewModel.userAuthenticatedStatus) {
            findNavController().navigate(R.id.action_todoFragment)
        }

        // Collecting email authentication responses and updating UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAuthorizedEmail.collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            binding.apply {
                                btLogin.visibility = View.INVISIBLE
                                emailProgressBar.visibility = View.VISIBLE
                            }
                        }
                        is Response.Success -> {
                            findNavController().navigate(R.id.action_todoFragment)
                        }
                        is Response.Error -> {
                            binding.apply {
                                btLogin.visibility = View.VISIBLE
                                emailProgressBar.visibility = View.GONE
                            }
                            Snackbar.make(
                                view, "${response.e?.message}", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // Collecting Google authentication responses and updating UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAuthorizedGoogle.collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            binding.apply {
                                googleProgressBar.visibility = View.VISIBLE
                                btLoginGoogle.visibility = View.INVISIBLE
                            }
                        }
                        is Response.Success -> {
                            findNavController().navigate(R.id.action_todoFragment)
                        }
                        is Response.Error -> {
                            binding.apply {
                                btLoginGoogle.visibility = View.VISIBLE
                                googleProgressBar.visibility = View.GONE
                            }
                            Snackbar.make(
                                view, "${response.e?.message}", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // Setting up click listeners for various actions
        binding.apply {
            // Navigation to sign up fragment
            tvRegister.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)
            }

            // Initiating Google sign-in flow
            btLoginGoogle.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                resultLauncher.launch(signInIntent)
            }

            // Initiating email sign-in flow
            btLogin.setOnClickListener {
                val signInEmail = binding.etLoginEmail.text.toString()
                val signInPassword = binding.etLoginPassword.text.toString()
                if (signInEmail.isNotEmpty() && signInPassword.isNotEmpty()) {
                    viewModel.signInUser(signInEmail, signInPassword)
                } else {
                    Snackbar.make(view, "Fill the required fields", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Clearing email and password fields onPause
    override fun onPause() {
        super.onPause()
        binding.apply {
            etLoginEmail.text = null
            etLoginPassword.text = null
        }
    }

    // Handling the result of Google sign-in
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.signInWithGoogle(result)
        }

    // Cleaning up view binding onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
