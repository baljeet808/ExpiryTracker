package com.baljeet.expirytracker.fragment.shared

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.databinding.FragmentIconsGalleryBinding
import com.baljeet.expirytracker.fragment.product.CustomViewModel
import com.baljeet.expirytracker.interfaces.OnIconSelected
import com.baljeet.expirytracker.listAdapters.SearchIconsAdapter


class IconsGallery : Fragment() , OnIconSelected {

    private lateinit var bind : FragmentIconsGalleryBinding
    private val viewModel : IconsViewModel by activityViewModels()

    private lateinit var iconsAdapter: SearchIconsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentIconsGalleryBinding.inflate(inflater, container, false)
        bind.apply {

            backButton.setOnClickListener { activity?.onBackPressed() }

            iconsAdapter=  SearchIconsAdapter(requireContext(),this@IconsGallery)
            iconsRecycler.layoutManager = GridLayoutManager(requireContext(),
                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4
                )
            iconsRecycler.adapter = iconsAdapter
            searchEdittext.doOnTextChanged { text, _, _, _ ->
                if (text.toString().count() > 0) {
                    viewModel.getIconByName(text.toString())
                } else {
                    viewModel.getAllIcons()
                }
            }
            viewModel.getAllIcons()
            viewModel.readAllData.observe(viewLifecycleOwner) {
                iconsAdapter.submitList(it)
                resultsCount.text =
                    requireContext().getString(R.string.number_of_results_var, it.size)
            }
        }
        return bind.root
    }

    override fun selectThisIcon(image: Image) {
        viewModel.selectedIcon= image
        activity?.onBackPressed()
    }
}