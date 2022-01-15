package com.baljeet.expirytracker.fragment.settings.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.FragmentManageProductsBinding
import com.baljeet.expirytracker.listAdapters.ProductResultsAdapter
import com.baljeet.expirytracker.listAdapters.SearchResultsAdapter

class ManageProducts : Fragment() {

    private lateinit var bind : FragmentManageProductsBinding
    private val viewModel : ProductViewModel by viewModels()
    private lateinit var resultsAdapter : ProductResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManageProductsBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }

            productsRecycler.layoutManager = GridLayoutManager(requireContext(),3)
            resultsAdapter = ProductResultsAdapter(requireContext())
            productsRecycler.adapter = resultsAdapter
            searchEdittext.doOnTextChanged { text, _, _, _ ->
                if(text.toString().count() > 0){
                    viewModel.searchByText(text.toString())
                }else{
                    viewModel.getAllProducts()
                }
            }

            viewModel.getAllProducts()

            viewModel.searchResults.observe(viewLifecycleOwner,{
                resultsAdapter.submitList(it)
                resultsCount.text = requireContext().getString(R.string.number_of_results_var,it.size)
            })

        }
        return bind.root
    }
}