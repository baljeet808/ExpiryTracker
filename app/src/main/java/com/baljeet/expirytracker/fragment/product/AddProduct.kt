package com.baljeet.expirytracker.fragment.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import com.baljeet.expirytracker.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddProduct : Fragment() {

    private lateinit var chooseCategory : TextInputEditText
    private lateinit var chooseCategoryLayout : TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        view.findViewById<ImageButton>(R.id.close_btn).setOnClickListener { activity?.onBackPressed() }

        chooseCategory = view.findViewById(R.id.choose_category_edittext)
        chooseCategoryLayout = view.findViewById(R.id.choose_category_layout)

        chooseCategory.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_addProduct_to_selectFrom)
        }

        return view
    }

}