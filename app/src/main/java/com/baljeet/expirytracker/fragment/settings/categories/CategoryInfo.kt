package com.baljeet.expirytracker.fragment.settings.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentCategoryInfoBinding
import com.baljeet.expirytracker.util.ImageConvertor

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

            when(category.image.mimeType){
                "asset"->{
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            resources.getIdentifier(
                                category.image.imageUrl,
                                "drawable",
                                requireContext().packageName
                            )
                        )
                    )
                }
                else->{
                    imageView.setImageBitmap(ImageConvertor.stringToBitmap(category.image.bitmap))
                }
            }
            nameEdittext.setText(category.category.categoryName)
        }
        return bind.root
    }

}