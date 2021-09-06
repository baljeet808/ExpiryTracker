package com.baljeet.expirytracker.fragment.dash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentDashBinding
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.TrackerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashFragment : Fragment() {

    private val productVM : ProductViewModel by activityViewModels()
    private val imageVm : ImageViewModel by activityViewModels()
    private val categoryVM : CategoryViewModel by activityViewModels()
    private val selectVM : SelectFromViewModel by activityViewModels()
    private val trackerVm : TrackerViewModel by activityViewModels()

    private lateinit var bind : FragmentDashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentDashBinding.inflate(inflater,container,false)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        bind.addProductButton.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_dashFragment_to_addProduct)
        }

        bind.trackerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        trackerVm.readAllTracker?.let {
            it.observe(viewLifecycleOwner, { its ->
                if(its.isNullOrEmpty()){
                    noItemView()
                }
                else{
                    bind.trackerLayout.visibility = View.VISIBLE
                    bind.noTrackerLayout.visibility = View.GONE
                    val arrayList = ArrayList<TrackerAndProduct>()
                    arrayList.addAll(its)
                    bind.trackerRecyclerView.adapter = TrackerAdapter(arrayList,requireContext())
                }
            })
        }
        seedData()

        return bind.root
    }
    private fun noItemView(){
        bind.noTrackerLayout.visibility = View.VISIBLE
        bind.trackerLayout.visibility = View.GONE
    }

    private fun seedData(){
        addAllImages()
        addAllCategories()
        addAllProducts()
    }

    private fun addAllCategories(){
        for(category in selectVM.getDefaultCategories()){
            categoryVM.addCategory(category)
        }
    }

    private fun addAllProducts(){
        for(product in selectVM.getAllProducts()){
            productVM.addProduct(product)
        }
    }

    private fun addAllImages(){
        for(image in selectVM.getImages()){
            imageVm.addImage(image)
        }
    }

}