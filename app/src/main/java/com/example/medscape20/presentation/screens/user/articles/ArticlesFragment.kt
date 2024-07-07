package com.example.medscape20.presentation.screens.user.articles

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    if (state.newsArticlesList.isNotEmpty()) {
                        binding.articlesRecylerView.layoutManager = LinearLayoutManager(context)
                        binding.articlesRecylerView.adapter = ArticlesNewsArticlesAdapter(
                            state.newsArticlesList,
                            requireContext(),
                            this@ArticlesFragment
                        )
                    }
                    if (state.isLoading) {
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.progressCircular.visibility = View.GONE
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

}