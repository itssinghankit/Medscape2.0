package com.example.medscape20.presentation.screens.user.customer.home.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentSourcesBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SourcesFragment : Fragment() {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!
    private var container: ViewGroup? = null
    private val viewModel: SourcesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSourcesBinding.inflate(layoutInflater, container, false)
        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility = View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }


        val adapter = SourcesTypesRecylerAdapter { position ->
            val action=SourcesFragmentDirections.actionSourcesFragmentToSourcesDescFragment(viewModel.states.value.data[position])
            findNavController().navigate(action)
        }
        binding.sourcesRecylcerView.layoutManager = LinearLayoutManager(context)
        binding.sourcesRecylcerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.states.collect { state ->
                    state.errMessage?.let { showMessage(it) }

                    if (state.isLoading) binding.progressCircular.visibility = View.VISIBLE
                    else binding.progressCircular.visibility = View.GONE

                    if(state.data.isNotEmpty()){
                        adapter.submitList(state.data)
                    }

                }

            }
        }
    }

    private fun showMessage(errorMessage: Int?) {
        errorMessage?.let {
            Snackbar.make(container!!.rootView, getString(errorMessage), Snackbar.LENGTH_SHORT)
                .show()
            viewModel.event(SourcesEvents.ResetErrorMessage)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}