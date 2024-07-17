package com.example.medscape20.presentation.screens.user.collector.customers

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentCustomersBinding
import com.example.medscape20.domain.usecase.user.collector.customers.CustomersViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomersFragment : Fragment() {

    private var _binding:FragmentCustomersBinding?=null
    private val binding get() = _binding!!
    private val viewModel:CustomersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding= FragmentCustomersBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        val adapter=CustomersRecyclerAdapter{uid->
            //will do something
        }
        binding.customersRecyclerView.layoutManager=LinearLayoutManager(context)
        binding.customersRecyclerView.adapter=adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect{state->

                    if(state.isLoading){
                        binding.progressCircular.visibility=View.VISIBLE
                        binding.mainContent.visibility=View.GONE
                    }else{
                        binding.progressCircular.visibility=View.GONE
                        binding.mainContent.visibility=View.VISIBLE
                    }

                    if(state.data.isNotEmpty()){
                        adapter.submitList(state.data)
                    }


                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}