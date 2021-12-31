package com.baljeet.expirytracker.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentDonateBinding


class DonateFragment : Fragment() {

    private lateinit var bind : FragmentDonateBinding

    private val viewModel : DonateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentDonateBinding.inflate(inflater,container,false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }
            candyCard.setOnClickListener {
                resetAllButtons()
                candyLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
                viewModel.selectCandy()
                candyCheck.visibility = View.VISIBLE
            }
            chocolateCard.setOnClickListener {
                resetAllButtons()
                chocolateLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
                viewModel.selectChocolate()
                chocolateCheck.visibility = View.VISIBLE
            }
            coffeeCard.setOnClickListener {
                resetAllButtons()
                coffeeLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
                viewModel.selectCoffee()
                coffeeCheck.visibility = View.VISIBLE
            }
            burgerCard.setOnClickListener {
                resetAllButtons()
                burgerLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
                viewModel.selectBurger()
                burgerCheck.visibility = View.VISIBLE
            }
            mealCard.setOnClickListener {
                resetAllButtons()
                mealLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
                viewModel.selectMeal()
                mealCheck.visibility = View.VISIBLE
            }
            donateButton.setOnClickListener { 
                Toast.makeText(requireContext(),"${viewModel.getDonationAmount()}", Toast.LENGTH_SHORT).show()
            }
        }
        return bind.root
    }

    private fun resetAllButtons(){
        bind.apply {
            candyLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
            chocolateLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
            coffeeLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
            burgerLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))
            mealLayout.setBackgroundColor(requireContext().getColor(R.color.tab_background))

            candyCheck.visibility = View.INVISIBLE
            chocolateCheck.visibility = View.INVISIBLE
            coffeeCheck.visibility = View.INVISIBLE
            burgerCheck.visibility = View.INVISIBLE
            mealCheck.visibility = View.INVISIBLE

            donateButton.isEnabled = true
        }
    }

}