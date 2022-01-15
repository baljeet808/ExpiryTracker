package com.baljeet.expirytracker.fragment.settings.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentCreateCustomBinding
import com.baljeet.expirytracker.databinding.FragmentManageCategoriesBinding

class ManageCategories : Fragment() {

    private lateinit var bind : FragmentManageCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManageCategoriesBinding.inflate(inflater, container, false)
        bind.apply {


        }
        return bind.root
    }


}