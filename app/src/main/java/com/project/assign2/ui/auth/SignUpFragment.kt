// Package declaration indicating the location of the file within the project
package com.project.assign2.ui.auth

// Import statements for necessary classes and interfaces
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
import com.project.assign2.R
import com.project.assign2.data.network.model.Response
import com.project.assign2.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Fragment for handling user sign-up functionality
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!! // View binding instance
    private val viewModel: AuthViewModel by viewModels() // ViewModel instance for authentication

    // Function to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Function to initialize views and set up click listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Collecting email authentication responses and updating UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAuthorizedEmail.collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            binding.apply {
                                btSignUp.visibility = View.INVISIBLE
                                emailProgressBar.visibility = View.VISIBLE
                            }
                        }
                        is Response.Success -> {
                            findNavController().navigate(R.id.action_todoFragment)
                        }
                        is Response.Error -> {
                            binding.apply {
                                btSignUp.visibility = View.VISIBLE
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

        // Setting up click listeners for various actions
        binding.apply {
            // Navigation back to previous fragment
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            // Initiating sign-up flow
            btSignUp.setOnClickListener {
                val signUpEmail = binding.etSignUpEmail.text.toString()
                val signUpPassword = binding.etSignUpPassword.text.toString()
                if (signUpEmail.isNotEmpty() && signUpPassword.isNotEmpty()) {
                    viewModel.signUpUser(signUpEmail, signUpPassword)
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
            etSignUpEmail.text = null
            etSignUpPassword.text = null
        }
    }

    // Cleaning up view binding onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
