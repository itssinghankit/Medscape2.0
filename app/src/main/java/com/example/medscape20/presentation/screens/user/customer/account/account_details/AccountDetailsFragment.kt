package com.example.medscape20.presentation.screens.user.customer.account.account_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountDetailsBinding

class AccountDetailsFragment : Fragment() {

    private var _binding: FragmentAccountDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: AccountDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountDetailsBinding.inflate(layoutInflater, container, false)
        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility = View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            name.text = args.details.name
            email.text = args.details.email
            gender.text =
                args.details.gender?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            address.text =
                args.details.address?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            city.text =
                args.details.city?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            state.text =
                args.details.state?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            mobile.text=args.details.mobile
            Glide.with(requireContext())
                .load(args.details.avatar)
                .into(avatar)
        }

    }

}