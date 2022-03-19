package com.baljeet.expirytracker.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentBeProBinding


enum class SubscribeType{
    MONTH,YEAR
}
class BePro : Fragment() {

    private lateinit var bind : FragmentBeProBinding

    private var subscribeFor : SubscribeType = SubscribeType.YEAR

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentBeProBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            backButton.setOnClickListener{
                activity?.onBackPressed()
            }

            monthlyCheckbox.setOnCheckedChangeListener { _, isChecked ->
                   updateCards(!isChecked)
                    yearlyCheckbox.isChecked = !isChecked
            }
            yearlyCheckbox.setOnCheckedChangeListener { _, isChecked ->
                updateCards(isChecked)
                monthlyCheckbox.isChecked = !isChecked
            }
        }
    }


    private fun updateCards(isYearly : Boolean){
            if(isYearly){
                bind.yearlyCard.strokeWidth = 2
                bind.monthlyCard.strokeWidth = 0
                subscribeFor = SubscribeType.YEAR
                bind.upgradePrice.text = getString(R.string._11_99)
            }else{
                bind.yearlyCard.strokeWidth = 0
                bind.monthlyCard.strokeWidth = 2
                subscribeFor = SubscribeType.MONTH
                bind.upgradePrice.text = getString(R.string._1_99)
            }
    }

}