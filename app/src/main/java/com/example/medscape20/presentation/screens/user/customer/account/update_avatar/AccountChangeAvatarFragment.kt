package com.example.medscape20.presentation.screens.user.customer.account.update_avatar

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountChangeAvatarBinding
import com.example.medscape20.presentation.screens.common.ImagePickerBottomSheet
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountChangeAvatarFragment : Fragment() {

    private var _binding:FragmentAccountChangeAvatarBinding?=null
    private val binding get() = _binding!!
    private val args:AccountChangeAvatarFragmentArgs by navArgs()
    private val viewModel:AccountChangeAvatarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding= FragmentAccountChangeAvatarBinding.inflate(layoutInflater, container, false)
        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility=View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility=View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("avatar"){_,bundle->
            val uri = bundle.getString("avatar_uri")
            uri?.let {
                val imageUri = Uri.parse(uri)
                viewModel.event(AccountChangeAvatarEvents.OnNewAvatarSelected(imageUri))
            }
        }

        //set previous avatar
        Glide.with(requireContext()).load(args.avatarUri).into(binding.avatar)

        binding.selectAvatarBtn.setOnClickListener {
            val bottomSheet=ImagePickerBottomSheet()
            bottomSheet.show(parentFragmentManager,"image picker")
        }

        binding.updateBtn.setOnClickListener {
            viewModel.event(AccountChangeAvatarEvents.OnUpdateAvatarClicked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.states.collect{states->
                    when(states.isLoading){
                        true -> {
                            binding.progressCircular.visibility=View.VISIBLE
                        }
                        false -> {
                            binding.progressCircular.visibility=View.GONE
                        }
                    }

                    states.url?.let {
                        navigateBackToAccountScreen(it)
                    }

                    states.newAvatar?.let{
                        Glide.with(requireContext()).load(it).into(binding.avatar)
                    }

                    states.errMessage?.let {
                        showError(states.errMessage)
                    }

                }
            }
        }

    }

    private fun showError(errorMessage: Int?) {
        errorMessage?.let {
            Snackbar.make(requireView(), getString(errorMessage), Snackbar.LENGTH_SHORT).show()
            viewModel.event(AccountChangeAvatarEvents.ResetErrorMessage)
        }
    }

    private fun navigateBackToAccountScreen(url: String) {
        setFragmentResult(UpdateAvatarEnum.UPDATE_AVATAR_REQUEST_KEY.name,Bundle().apply {
            putString(UpdateAvatarEnum.UPDATE_AVATAR_AVATAR_URL.name,url)
        })
        findNavController().navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}

enum class UpdateAvatarEnum{
    UPDATE_AVATAR_REQUEST_KEY,
    UPDATE_AVATAR_AVATAR_URL
}