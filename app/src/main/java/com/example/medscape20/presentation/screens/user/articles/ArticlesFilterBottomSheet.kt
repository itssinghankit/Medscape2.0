package com.example.medscape20.presentation.screens.user.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import com.example.medscape20.R
import com.example.medscape20.databinding.ArticlesBottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ArticlesFilterBottomSheet :
    BottomSheetDialogFragment() {

    lateinit var binding: ArticlesBottomSheetFilterBinding
    lateinit var countriesMap: Map<String, String>
    lateinit var countryAbbreviation: String
    lateinit var category: NewsCategory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ArticlesBottomSheetFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting arguments to apply for previously selected filter
        countryAbbreviation = arguments?.getString(FilterArgs.COUNTRY.value) ?: "all"
        val categoryValue =
            arguments?.getString(FilterArgs.CATEGORY.value) ?: NewsCategory.ALL.value
        category = NewsCategory.entries.find { it.value == categoryValue } ?: NewsCategory.ALL

        setPreviousFilter()

        //Logic for setting new filter
        setNewFilter()

        //setting the filter listener
        binding.apply.setOnClickListener {
            setFragmentResult(FilterArgs.REQUEST_KEY.value, Bundle().apply {
                putString(FilterArgs.COUNTRY.value, countryAbbreviation)
                putString(FilterArgs.CATEGORY.value, category.value)
            })
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setNewFilter() {
        //only top headlines is allowed with country
        binding.category.setOnCheckedChangeListener { _, check_id ->
            when (check_id) {
                R.id.all -> {
                    category = NewsCategory.ALL
                    binding.countrySpinner.setSelection(0)
                }

                R.id.top_headlines -> category = NewsCategory.TOP_HEADLINES
            }
        }

        binding.countrySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //that is for initial setup when radio button is set to all
                if (position != 0) {
                    binding.topHeadlines.isChecked = true
                }
                countryAbbreviation = countriesMap.values.toList()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

        }
    }

    private fun setPreviousFilter() {
        when (category) {
            NewsCategory.ALL -> {
                binding.all.isChecked = true
            }

            NewsCategory.TOP_HEADLINES -> {
                binding.topHeadlines.isChecked = true
            }
        }

        setAdapterToCountrySpinner()

        countriesMap.onEachIndexed { index, entry ->
            if (entry.value == countryAbbreviation) {
                binding.countrySpinner.setSelection(index)
                return@onEachIndexed
            }
        }
    }

    private fun setAdapterToCountrySpinner() {
        countriesMap = mapOf(
            "All" to "all",
            "Argentina" to "ar",
            "Australia" to "au",
            "Austria" to "at",
            "Belgium" to "be",
            "Brazil" to "br",
            "Bulgaria" to "bg",
            "Canada" to "ca",
            "China" to "cn",
            "Colombia" to "co",
            "Cuba" to "cu",
            "Czech Republic" to "cz",
            "Egypt" to "eg",
            "France" to "fr",
            "Germany" to "de",
            "Greece" to "gr",
            "Hong Kong" to "hk",
            "Hungary" to "hu",
            "India" to "in",
            "Indonesia" to "id",
            "Ireland" to "ie",
            "Israel" to "il",
            "Italy" to "it",
            "Japan" to "jp",
            "Latvia" to "lv",
            "Lithuania" to "lt",
            "Malaysia" to "my",
            "Mexico" to "mx",
            "Morocco" to "ma",
            "Netherlands" to "nl",
            "New Zealand" to "nz",
            "Nigeria" to "ng",
            "Norway" to "no",
            "Philippines" to "ph",
            "Poland" to "pl",
            "Portugal" to "pt",
            "Romania" to "ro",
            "Russia" to "ru",
            "Saudi Arabia" to "sa",
            "Serbia" to "rs",
            "Singapore" to "sg",
            "Slovakia" to "sk",
            "Slovenia" to "si",
            "South Africa" to "za",
            "South Korea" to "kr",
            "Sweden" to "se",
            "Switzerland" to "ch",
            "Taiwan" to "tw",
            "Thailand" to "th",
            "Turkey" to "tr",
            "United Arab Emirates" to "ae",
            "Ukraine" to "ua",
            "United Kingdom" to "gb",
            "United States" to "us",
            "Venezuela" to "ve"
        )
        val countryArray = countriesMap.keys.toList()
        val adapter =
            ArrayAdapter(
                requireContext(),
                R.layout.articles_bottom_sheet_spinner_item,
                countryArray
            ).also {
                it.setDropDownViewResource(R.layout.articles_bottom_sheet_spinner_item)
            }
        binding.countrySpinner.adapter = adapter

//        val position:Int=adapter.getPosition(countriesMap.filterValues { it == countryAbbreviation }.keys.first())

    }

}

enum class NewsCategory(val value: String) {
    ALL("everything"),
    TOP_HEADLINES("top-headlines")
}

enum class FilterArgs(val value: String) {
    REQUEST_KEY("filter"),
    COUNTRY("country"),
    CATEGORY("category")
}
