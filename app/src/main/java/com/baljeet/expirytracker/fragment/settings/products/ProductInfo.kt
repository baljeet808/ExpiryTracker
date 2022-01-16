package com.baljeet.expirytracker.fragment.settings.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.FragmentImageEditorBinding
import com.baljeet.expirytracker.databinding.FragmentProductInfoBinding


class ProductInfo : Fragment() {

    private lateinit var bind : FragmentProductInfoBinding
    private val viewModel : ProductViewModel by viewModels()
    private val navArgs: ProductInfoArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentProductInfoBinding.inflate(inflater, container, false)
        bind.apply {
            closeBtn.setOnClickListener { activity?.onBackPressed() }
            val product = navArgs.productAndImage
            fragmentTitleText.text = product.product.name
        }
        return bind.root
    }


}