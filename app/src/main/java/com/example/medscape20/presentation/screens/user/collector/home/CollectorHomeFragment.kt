package com.example.medscape20.presentation.screens.user.collector.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medscape20.databinding.FragmentCollectorHomeBinding
import kotlinx.coroutines.launch

class CollectorHomeFragment : Fragment() {

    private var _binding: FragmentCollectorHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CollectorHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorHomeBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.peopleCount.animateToValue(1029)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.state.collect{state->
                    if(state.isLoading){
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.mainView.visibility = View.GONE
                    }else{
                        binding.progressCircular.visibility = View.GONE
                        binding.mainView.visibility = View.VISIBLE
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