package com.project.assign2.ui.profile
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
import com.project.assign2.data.network.model.Response
import com.project.assign2.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // View binding for accessing views in the layout
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModel instance associated with this fragment
    private val viewModel: ProfileViewModel by viewModels()

    // Inflates the layout and initializes view binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Initializes UI components and sets up event listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up toolbar with the appropriate title
        setToolbar()

        // Observes changes in user profile details and updates UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfileDetails.collect { profile ->
                    binding.tvUserEmail.text = profile?.email
                }
            }
        }

        // Observes changes in user profile response and updates UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            // Show progress indicator and hide action buttons
                            binding.apply {
                                btnDeleteAcc.visibility = View.INVISIBLE
                                btnLogout.visibility = View.INVISIBLE
                                profileProgressBar.visibility = View.VISIBLE
                            }
                        }
                        is Response.Success -> {
                            // Navigate to sign-in fragment upon successful profile update
                            val action = ProfileFragmentDirections.actionProfileFragmentToSignInFragment()
                            findNavController().navigate(action)
                        }
                        is Response.Error -> {
                            // Show action buttons, hide progress indicator, and display error message
                            binding.apply {
                                btnDeleteAcc.visibility = View.VISIBLE
                                btnLogout.visibility = View.VISIBLE
                                profileProgressBar.visibility = View.GONE
                            }
                            Snackbar.make(
                                view,
                                "${response.e?.message}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // Set click listeners for logout and delete account buttons
        binding.apply {
            btnLogout.setOnClickListener {
                viewModel.signOutUser()
            }

            btnDeleteAcc.setOnClickListener {
                viewModel.deleteUser()
            }
        }
    }

    // Sets the title of the toolbar based on the current destination
    private fun setToolbar() {
        binding.profileFragmentToolbar.title = findNavController().currentDestination?.label
    }

    // Clears the view binding reference to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
