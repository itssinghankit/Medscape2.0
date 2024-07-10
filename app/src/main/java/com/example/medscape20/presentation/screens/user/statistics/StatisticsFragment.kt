package com.example.medscape20.presentation.screens.user.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.medscape20.R
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class StatisticsFragment : Fragment() {

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

    fun performSearch(query: String) {
        // Implement your search logic here

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_statistics, container, false)

        val searchBar = view.findViewById<SearchBar>(R.id.searchBar)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val searchResultsRecyclerView = view.findViewById<RecyclerView>(R.id.searchResultsRecyclerView)

        searchView.setupWithSearchBar(searchBar)



//        searchBar.setOnClickListener {
//            searchView.show()
//        }
        return view
    }


}
