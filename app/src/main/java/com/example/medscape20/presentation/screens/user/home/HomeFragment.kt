package com.example.medscape20.presentation.screens.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentHomeBinding
import com.example.medscape20.domain.models.ArticleModel
import com.google.android.material.snackbar.Snackbar
//import com.example.medscape20.presentation.screens.user.UserFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(),OnHomeArticleClicked {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //search view logic
        binding.searchView.setupWithSearchBar(binding.searchBar)
val arraylist= arrayListOf<ArticleModel>(
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    ),
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    ),
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    ),
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    ),
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    ),
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    ),
    ArticleModel(
        author = "ankit",
        content = "null",
        description = "null",
        publishedAt = "null",
        source = "null",
        title = "null",
        url = "null",
        urlToImage = "null"
    )
)
        binding.searchResultsRecyclerView.layoutManager=LinearLayoutManager(context)
        binding.searchResultsRecyclerView.adapter=HomeSearchArticleAdapter(arraylist)


        binding.avatar.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_webViewArticleFragment)
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

                viewModel.state.collect {state->
                    state.avatar?.let { avatar ->
                        Glide.with(this@HomeFragment).load(avatar).into(binding.avatar)
                    }

                    if(state.isError){
                        showError(state.errMessage)
                    }

                    if(state.isLoading){
                        binding.progressCircular.visibility=View.VISIBLE
                        binding.mainLayout.visibility=View.INVISIBLE
                    }else{
                        binding.progressCircular.visibility=View.GONE
                        binding.mainLayout.visibility=View.VISIBLE
                    }
                    if(state.newsArticlesList.isNotEmpty()){
                        //horizontal layout manager
                        binding.articlesRecyclerView.layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        binding.articlesRecyclerView.adapter=HomeNewsArticlesAdapter(state.newsArticlesList,requireContext(),this@HomeFragment)
                    }
                }
            }
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

    override fun onClicked(url: String) {
        super.onClicked(url)
        val action = HomeFragmentDirections.actionHomeFragmentToWebViewArticleFragment(url)
        findNavController().navigate(action)

    }

}
enum class Category(val value: String) {
    BIODEGRADABLE("Biodegradable Waste"),
    NON_BIODEGRADABLE("Non Biodegradable Waste")
}