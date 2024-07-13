package com.example.medscape20.presentation.screens.user.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentStatisticsBinding
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel:StatisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        searchView.addTransitionListener { searchView, previousState, newState ->
//            if (newState == SearchView.TransitionState.SHOWING) {
//                // Do something when SearchView is shown
//            } else if (newState == SearchView.TransitionState.HIDING) {
//                // Do something when SearchView is hidden
//            }
//        }
//
//        searchView.editText.setOnEditorActionListener { textView, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                performSearch(searchView.text.toString())
//                true
//            } else {
//                false
//            }
//
//    }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding= FragmentStatisticsBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            getIncomeWasteData()
            getRegionWasteData()
            getWasteCompositionData()
            getIndiaWasteTreatmentData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }


}
