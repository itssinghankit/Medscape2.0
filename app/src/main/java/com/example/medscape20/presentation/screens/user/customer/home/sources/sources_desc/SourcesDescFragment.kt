package com.example.medscape20.presentation.screens.user.customer.home.sources.sources_desc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentSourcesDescBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SourcesDescFragment : Fragment() {

    private var _binding: FragmentSourcesDescBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SourcesDescViewModel by viewModels()
    private val args: SourcesDescFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSourcesDescBinding.inflate(layoutInflater, container, false)
        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility = View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility = View.GONE

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        //save data from sources screen
        viewModel.event(SourcesDescEvents.SetSourcesData(args.data))

        val adapter = SourcesDescAdapter()
        binding.subtopicRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.subtopicRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    if (state.sourcesDesc.isNotEmpty()) {
                        adapter.submitList(state.sourcesDesc)
                        Glide.with(requireContext()).load(state.image).into(binding.img)
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}