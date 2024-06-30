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
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAvatarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: AvatarViewModel by viewModels()

//    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        if (uri != null) {
//            // Handle the selected image URI
//            avatarUri=uri
//            binding.avatarImg.setImageURI(uri) // Set the image to an ImageView (example)
//        }
//    }


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
            findNavController().navigate(R.id.homeActivity)
        }

        binding.slctAvatar.setOnClickListener {
            val imagePickerBottomSheet = ImagePickerBottomSheet()
            imagePickerBottomSheet.show(parentFragmentManager, "imagePickerBottomSheet")
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewmodel.avatarUri.collect {
                    binding.avatarImg.setImageURI(it)
                }

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}