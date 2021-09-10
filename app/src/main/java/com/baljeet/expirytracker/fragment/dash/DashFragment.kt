package com.baljeet.expirytracker.fragment.dash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var handlerAnimation = Handler(Looper.getMainLooper())

    private lateinit var bind : FragmentDashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentDashBinding.inflate(inflater,container,false)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        bind.addProductFab.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_dashFragment_to_addProduct)
        }
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
                    bind.addProductFab.visibility = View.VISIBLE
                    bind.imageForAnimation.visibility = View.VISIBLE
                    bind.addProductButton.visibility = View.GONE
                    runnable.run()
                    val arrayList = ArrayList<TrackerAndProduct>()
                    arrayList.addAll(its)
                    bind.trackerRecyclerView.adapter = TrackerAdapter(arrayList,requireContext())
                }
            })
        }
        seedData()


        bind.trackerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                bind.addProductFab.apply {
                    //scroll down
                    if(dy > 30  && isExtended ){
                        shrink()
                    }
                    //reached the top of list
                    if(!recyclerView.canScrollVertically(-1)){
                        extend()
                    }
                }

            }
        })

        return bind.root
    }
    private fun noItemView(){
        bind.noTrackerLayout.visibility = View.VISIBLE
        bind.trackerLayout.visibility = View.GONE
        bind.addProductFab.visibility = View.GONE
        bind.imageForAnimation.visibility = View.GONE
        bind.addProductButton.visibility = View.VISIBLE
        handlerAnimation.removeCallbacks(runnable)
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

    override fun onDestroy() {
        super.onDestroy()
        handlerAnimation.removeCallbacks(runnable)
    }


    private var runnable = object : Runnable{
        override fun run() {
            bind.imageForAnimation.apply {
                animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(1200)
                    .withEndAction{
                        scaleX = 1f
                        scaleY = 1f
                        alpha = 1f
                    }
            }
            handlerAnimation.postDelayed(this,1500)
        }

    }

}