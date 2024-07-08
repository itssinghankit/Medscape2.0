package com.example.medscape20.presentation.screens.user.articles

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medscape20.databinding.FragmentArticlesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticlesFragment : Fragment(), OnArticlesArticleClicked {

    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticlesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(FilterArgs.REQUEST_KEY.value) { _, bundle ->
            val country = bundle.getString(FilterArgs.COUNTRY.value)
            val countryAbbreviation = bundle.getString(FilterArgs.CATEGORY.value)

            if (country != null && countryAbbreviation != null) {
                viewModel.event(ArticlesEvents.OnFilterSet(countryAbbreviation, country))
            }

        }

        //setting the recycler view
        binding.articlesRecylerView.layoutManager = LinearLayoutManager(context)
        val adapter = ArticlesNewsArticlesAdapter(
            viewModel.state.value.newsArticlesList,
            requireContext(),
            this@ArticlesFragment
        )
        binding.articlesRecylerView.adapter = adapter

        //setting the filter listener
        binding.filter.setOnClickListener {

            //we have to pass data like this so as persist configuration changes
            val bottomSheet = ArticlesFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(FilterArgs.COUNTRY.value, viewModel.state.value.countryAbbreviation)
                    putString(FilterArgs.CATEGORY.value, viewModel.state.value.category)
                }
            }
            bottomSheet.show(parentFragmentManager, "filter")
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->

                    //no condition as we want it to run on either case
                    adapter.updateData(state.newsArticlesList)

                    if (state.isLoading) {
                        binding.emptyResult.visibility=View.GONE
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.progressCircular.visibility = View.GONE
                    }

                    if(state.showResultNullError){
                        binding.emptyResult.visibility=View.VISIBLE
                    }else{
                        binding.emptyResult.visibility=View.GONE
                    }

                }
            }
        }
    }


    override fun onClicked(url: String) {
        val action = ArticlesFragmentDirections.actionArticlesFragmentToWebViewArticleFragment(url)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private data class Filters(
        val country: String? = null,
        val category: NewsCategory? = null
    )

}