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
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.TrackerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashFragment : Fragment() {

    private val productVM : ProductViewModel by activityViewModels()
    private val imageVm : ImageViewModel by activityViewModels()
    private val categoryVM : CategoryViewModel by activityViewModels()
    private val selectVM : SelectFromViewModel by activityViewModels()
    private val trackerVm : TrackerViewModel by activityViewModels()

    private lateinit var statusText : TextView
    private lateinit var greetingText : TextView
    private lateinit var trackerRecycler : RecyclerView
    private lateinit var infoImage : ImageView
    private lateinit var slogan : TextView

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

        statusText = view.findViewById(R.id.status_text)
        greetingText = view.findViewById(R.id.greeting_text)
        trackerRecycler = view.findViewById(R.id.tracker_recycler_view)
        trackerRecycler.layoutManager = LinearLayoutManager(requireContext())

        infoImage = view.findViewById(R.id.illustration_view)
        slogan = view.findViewById(R.id.slogal_text)

        trackerVm.readAllTracker?.let {
            it.observe(viewLifecycleOwner, { its ->
                if(its.isNotEmpty()){
                    statusText.visibility = View.VISIBLE
                    greetingText.visibility = View.VISIBLE
                    trackerRecycler.visibility = View.VISIBLE
                    infoImage.visibility = View.GONE
                    slogan.visibility = View.GONE
                    greetingText.text = resources.getString(R.string.greeting_text)
                    val arrayList = ArrayList<TrackerAndProduct>()
                    arrayList.addAll(its)
                    trackerRecycler.adapter = TrackerAdapter(arrayList,requireContext())
                }
            })
        }
        seedData()

        return view
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