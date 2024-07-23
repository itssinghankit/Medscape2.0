package com.example.medscape20.presentation.screens.user.customer.home.category

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentCategoryBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val args: CategoryFragmentArgs by navArgs()

    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility = View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility = View.GONE

        viewModel.getCategoryData(args.category)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    state.image?.let { url ->

                        //it means all data is fetched from server so assign it
                        Glide.with(this@CategoryFragment).load(url).into(binding.img)

                        binding.subtopicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        binding.subtopicRecyclerView.adapter = CategoryRecyclerViewAdapter(state.categoryDescription)

                        // Add top and bottom space to the recycler view for first first element and last element
                        val topHeight = resources.getDimensionPixelSize(R.dimen.top_space)
                        val botHeight = resources.getDimensionPixelSize(R.dimen.bot_space)
                        binding.subtopicRecyclerView.post {
                            binding.subtopicRecyclerView.addItemDecoration(
                                TopBottomSpaceItemDecoration(
                                    topHeight,
                                    botHeight
                                )
                            )
                        }

                    }
                    if (state.isError) {
                        showError(state.errMessage)
                    }

                    //progress circular
                    if (!state.isLoading) {
                        binding.progressCircular.visibility = View.GONE
                    }else{
                        binding.progressCircular.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showError(errorMessage: Int?) {
        errorMessage?.let {
            Snackbar.make(requireView(), getString(errorMessage), Snackbar.LENGTH_SHORT).show()
            viewModel.event(CategoryEvents.ResetErrorMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}