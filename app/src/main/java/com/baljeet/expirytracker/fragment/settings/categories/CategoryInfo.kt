package com.baljeet.expirytracker.fragment.settings.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentCategoryInfoBinding

class CategoryInfo : Fragment() {

    private lateinit var bind : FragmentCategoryInfoBinding
    private val viewModel : CategoryViewModel by viewModels()
    private val navArgs : CategoryInfoArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCategoryInfoBinding.inflate(inflater, container, false)
        bind.apply {
           bind.closeBtn.setOnClickListener { activity?.onBackPressed() }
            val category = navArgs.categoryAndImage
            fragmentTitleText.text = category.category.categoryName
        }
        return bind.root
    }

}