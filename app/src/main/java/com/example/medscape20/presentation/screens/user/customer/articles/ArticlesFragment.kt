package com.example.medscape20.presentation.screens.user.customer.articles

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentArticlesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticlesFragment : Fragment(){

    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticlesViewModel by viewModels()
    private lateinit var articlesAdapter: ArticlesNewsArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility=View.VISIBLE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility=View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(FilterArgs.REQUEST_KEY.value) { _, bundle ->
            val country = bundle.getString(FilterArgs.COUNTRY.value)
            val countryAbbreviation = bundle.getString(FilterArgs.CATEGORY.value)

            if (country != null && countryAbbreviation != null) {
                viewModel.searchTxt.value=""
                viewModel.event(ArticlesEvents.OnFilterSet(countryAbbreviation, country))
            }

        }

        //setting the recycler view
        setupRecyclerView()

        binding.searchTxt.doOnTextChanged{text,_,_,_->
            if(!text.isNullOrEmpty()){
                viewModel.event(ArticlesEvents.SearchArticles)
            }
        }

        binding.searchTxt.setOnEditorActionListener { v, actionId, event->
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                hideKeyboard()
                true
            }else{
                false
            }
        }

        //setting the filter listener
        binding.searchBar.setEndIconOnClickListener {

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

                    if (state.isLoading) {
                        binding.emptyResult.visibility=View.GONE
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.progressCircular.visibility = View.GONE
                    }

                    if(state.showResultNullError){
                        articlesAdapter.submitList(emptyList())
                        binding.emptyResult.visibility=View.VISIBLE
                    }else{
                        binding.emptyResult.visibility=View.GONE
                    }

                    if(state.newsArticlesList.isNotEmpty()){
                        articlesAdapter.submitList(state.newsArticlesList)
                    }

                }
            }
        }
    }


    private fun setupRecyclerView() {
        articlesAdapter = ArticlesNewsArticlesAdapter { url ->
            val action = ArticlesFragmentDirections.actionArticlesFragmentToWebViewArticleFragment(url)
            findNavController().navigate(action)
        }
        binding.articlesRecylerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesAdapter
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Activity.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}