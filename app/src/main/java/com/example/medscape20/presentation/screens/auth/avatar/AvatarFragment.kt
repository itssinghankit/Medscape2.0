package com.example.medscape20.presentation.screens.auth.avatar

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAvatarBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: AvatarViewModel by viewModels()
    private val args: AvatarFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvatarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //from bottom sheet
        setFragmentResultListener("avatar") { requestKey, bundle ->
            val uri = bundle.getString("avatar_uri")
            uri?.let {
                val imageUri = Uri.parse(uri)
                viewmodel.event(AvatarEvents.OnAvatarSelected(imageUri))
            }
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.signupBtn.setOnClickListener {
            viewmodel.event(AvatarEvents.OnSignupClicked(args))
        }

        binding.slctAvatar.setOnClickListener {
            val imagePickerBottomSheet = ImagePickerBottomSheet()
            imagePickerBottomSheet.show(parentFragmentManager, "imagePickerBottomSheet")
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.state.collect {
                    if(it.isError) showError(it.errorMessage)
                    if(it.navigateToUserScreen) navigateToUserScreen()
                    if(it.avatarUri != null) binding.avatarImg.setImageURI(it.avatarUri)
                    if(it.isLoading){
                        binding.progressCircular.visibility=View.VISIBLE
                        binding.signupBtn.visibility=View.INVISIBLE
                    }else{
                        binding.progressCircular.visibility= View.GONE
                        binding.signupBtn.visibility= View.VISIBLE
                    }

                }
            }
        }
    }

    private fun navigateToUserScreen() {
        //change navigateToNextScreen variable false for back stack
        viewmodel.event(AvatarEvents.OnNavigationDone)
        //navigate to home screen
        findNavController().navigate(R.id.action_avatarFragment_to_userFragment)
    }

    fun showError(errorMessage: Int?) {

        errorMessage?.let {
            Snackbar.make(requireView(), getString(errorMessage), Snackbar.LENGTH_SHORT).show()
            viewmodel.event(AvatarEvents.OnErrorShown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}