package com.baljeet.expirytracker.fragment.dash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.CategoryViewModel
import com.baljeet.expirytracker.data.ImageViewModel
import com.baljeet.expirytracker.data.ProductViewModel
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashFragment : Fragment() {

    private val productVM : ProductViewModel by activityViewModels()
    private val imageVm : ImageViewModel by activityViewModels()
    private val categoryVM : CategoryViewModel by activityViewModels()
    private val selectVM : SelectFromViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dash, container, false)

        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.add_product_button).setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_dashFragment_to_addProduct)
        }
       // seedData()

        return view
    }

    private fun seedData(){
        addAllImages()
        addAllCategories()
        addAllProducts()
    }

    private fun addAllCategories(){
        for(category in selectVM.getAllCategories()){
            categoryVM.addCategory(category)
        }
    }

    private fun addAllProducts(){
        for(product in selectVM.getAllProducts()){
            productVM.addProduct(
                product
            )
        }
    }

    private fun addAllImages(){
        for(image in selectVM.getImages()){
            imageVm.addImage(image)
        }
    }

}