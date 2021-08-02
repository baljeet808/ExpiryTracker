package com.baljeet.expirytracker.fragment.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.listAdapters.OptionsAdapter
import com.baljeet.expirytracker.model.Category
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

private const val ARG_TITLE = "Category"

class SelectFrom : Fragment(),OptionsAdapter.OnOptionSelectedListener {

    private lateinit var customBox : TextInputLayout
    private lateinit var customEditBox : TextInputEditText
    private lateinit var titleText : TextView
    private lateinit var optionsRecycler : RecyclerView
    private lateinit var adapter : OptionsAdapter
    private val categories = ArrayList<Category>()

    private val viewModel : SelectFromViewModel by activityViewModels()

    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_select_from, container, false)

        customEditBox = view.findViewById(R.id.custom_edittext)
        customBox = view.findViewById(R.id.custom_box_layout)
        customBox.hint = resources.getString(R.string.product_var,title)
        titleText = view.findViewById(R.id.title_text)
        titleText.text = resources.getString(R.string.choose_title,title)
        optionsRecycler = view.findViewById(R.id.options_recycler)
        optionsRecycler.layoutManager = GridLayoutManager(requireContext(),3)



        adapter = if(title.equals("Category")){
            categories.addAll(viewModel.getAllCategories())
            OptionsAdapter(categories,requireContext(),null,this,null)
        }else{
            OptionsAdapter(null,requireContext(),null,this,null)
        }
        optionsRecycler.adapter = adapter
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) =
            SelectFrom().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                }
            }
    }

    override fun onOptionSelected(position: Int, visibility: Int) {
        if(visibility == View.GONE){
            customEditBox.setText(categories[position].categoryName)
            viewModel.setSelectedCategory(categories[position])
        }
        else{
            customEditBox.text?.clear()
            viewModel.setSelectedCategory(null)
        }
    }
}