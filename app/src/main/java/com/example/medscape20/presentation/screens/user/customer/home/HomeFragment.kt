package com.example.medscape20.presentation.screens.user.customer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentHomeBinding
import com.example.medscape20.presentation.screens.user.customer.account.update_avatar.UpdateAvatarEnum
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeRecyclerAdapter: HomeNewsArticlesAdapter
    private lateinit var searchRecyclerAdapter: HomeSearchArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility=View.VISIBLE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility=View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(UpdateAvatarEnum.UPDATE_AVATAR_REQUEST_KEY.name){ _, bundle->
            val avatar = bundle.getString(UpdateAvatarEnum.UPDATE_AVATAR_AVATAR_URL.name)
            avatar?.let{
                viewModel.event(HomeEvents.OnAvatarUpdation(it))
            }
        }

        //setting up articles recyclerview
        setupHomeRecyclerView()

        //setting up search recyclerview
        setupSearchRecyclerView()

        //search view logic
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                viewModel.event(HomeEvents.GetNewsArticles(text.toString()))
            }

        }

        //for category screens
        binding.biodegradable.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToCategoryFragment(Category.BIODEGRADABLE.value)
            findNavController().navigate(action)
        }
        binding.nonbiodegradable.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToCategoryFragment(Category.NON_BIODEGRADABLE.value)
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    state.avatar?.let { avatar ->
                        Glide.with(this@HomeFragment).load(avatar).into(binding.avatar)
                    }

                    if (state.isError) {
                        showError(state.errMessage)
                    }

                    if (state.isLoading) {
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.mainLayout.visibility = View.INVISIBLE
                    } else {
                        binding.progressCircular.visibility = View.GONE
                        binding.mainLayout.visibility = View.VISIBLE
                    }

                    if (state.newsArticlesList.isNotEmpty()) {
                        homeRecyclerAdapter.submitList(state.newsArticlesList) {
                            binding.articlesRecyclerView.scrollToPosition(0)
                        }
                    }

                    if (state.isSearching) {
                        binding.seachingProgressCircular.visibility = View.VISIBLE
                    } else {
                        binding.seachingProgressCircular.visibility = View.GONE
                    }

                    if (state.searchNewsArticleList.isNotEmpty()) {
                        searchRecyclerAdapter.submitList(state.searchNewsArticleList) {
                            binding.searchResultsRecyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
        }

    }

    private fun setupHomeRecyclerView() {
        homeRecyclerAdapter = HomeNewsArticlesAdapter{ url ->
            //on click navigating to show complete article
            val action = HomeFragmentDirections.actionHomeFragmentToWebViewArticleFragment(url)
            findNavController().navigate(action)
        }
        binding.articlesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = homeRecyclerAdapter
        }
    }

    private fun setupSearchRecyclerView() {
        searchRecyclerAdapter = HomeSearchArticleAdapter { url ->
            val action = HomeFragmentDirections.actionHomeFragmentToWebViewArticleFragment(url)
            findNavController().navigate(action)
        }
        binding.searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchRecyclerAdapter
        }
    }

    private fun showError(errorMessage: Int?) {

        errorMessage?.let {
            Snackbar.make(requireView(), getString(errorMessage), Snackbar.LENGTH_SHORT).show()
            viewModel.event(HomeEvents.ResetErrorMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

enum class Category(val value: String) {
    BIODEGRADABLE("Biodegradable Waste"),
    NON_BIODEGRADABLE("Non Biodegradable Waste")
}