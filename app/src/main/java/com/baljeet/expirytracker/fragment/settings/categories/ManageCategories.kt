package com.baljeet.expirytracker.fragment.settings.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentCreateCustomBinding
import com.baljeet.expirytracker.databinding.FragmentManageCategoriesBinding
import com.baljeet.expirytracker.listAdapters.SearchResultsAdapter

class ManageCategories : Fragment() {

    private lateinit var bind : FragmentManageCategoriesBinding
    private val viewModel : CategoryViewModel by viewModels()
    private lateinit var resultsAdapter : SearchResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManageCategoriesBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }

            categoriesRecycler.layoutManager = GridLayoutManager(requireContext(),3)
            resultsAdapter = SearchResultsAdapter(requireContext())
            categoriesRecycler.adapter = resultsAdapter
            searchEdittext.doOnTextChanged { text, _, _, _ ->
                if(text.toString().count() > 0){
                    viewModel.searchCategoryByWord(text.toString())
                }else{
                   viewModel.showAllAsResult()
                }
            }

            viewModel.showAllAsResult()

            viewModel.searchResults.observe(viewLifecycleOwner,{
                resultsAdapter.submitList(it)
                resultsCount.text = requireContext().getString(R.string.number_of_results_var,it.size)
            })
        }
        return bind.root
    }


}