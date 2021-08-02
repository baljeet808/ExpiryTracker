package com.baljeet.expirytracker.fragment.shared

import androidx.lifecycle.ViewModel
import com.baljeet.expirytracker.model.Category
import com.baljeet.expirytracker.model.Image

class SelectFromViewModel: ViewModel() {


    private var selectedCategory :Category? =  null
    private val categoryList  = ArrayList<Category>()
    private fun addToCategories(categories : ArrayList<Category>){categoryList.addAll(categories)}
    fun getAllCategories(): ArrayList<Category>{return categoryList}
    fun addSingleCategory(category : Category){categoryList.add(category)}
    fun removeSingleCategory(category : Category){categoryList.remove(category)}
    fun setSelectedCategory(category: Category?){selectedCategory = category}
    fun getSelectedCategory(): Category?{return selectedCategory}

    init {
        addToCategories(getDefaultCategories())
    }

    private fun getDefaultCategories(): ArrayList<Category>{
        val defaultCategories = ArrayList<Category>()

        val fruits = Category(
            1,
            "Fruit",
            Image(
                1,
                "fruits",
                "fruits",
                "fruits"
            )
        )
        val vegetables = Category(
            2,
            "Vegetable",
            Image(
                2,
                "vegetable",
                "vegetables",
                "vegetables"
            )
        )
        val meat = Category(
            3,
            "Meat",
            Image(
                3,
                "meat",
                "meat",
                "meat"
            )
        )
        val document = Category(
            4,
            "Document",
            Image(
                4,
                "document",
                "document",
                "document"
            )
        )
        val subscription = Category(
            5,
            "Subscription",
            Image(
                5,
                "subscription",
                "subscription",
                "subscription"
            )
        )
        val packedFood = Category(
            6,
            "Packed Food",
            Image(
                6,
                "packed_food",
                "packed_food",
                "packed_food"
            )
        )
        val liquor = Category(
            7,
            "Liquor",
            Image(
                7,
                "liquor",
                "liquor",
                "liquor"
            )
        )
        val drinks = Category(
            8,
            "Drinkable",
            Image(
                8,
                "drinks",
                "drinks",
                "drinks"
            )
        )
        val fastFood = Category(
            9,
            "Fast Food",
            Image(
                9,
                "fast_food",
                "fast_food",
                "fast_food"
            )
        )
        defaultCategories.add(fruits)
        defaultCategories.add(vegetables)
        defaultCategories.add(meat)
        defaultCategories.add(subscription)
        defaultCategories.add(liquor)
        defaultCategories.add(document)
        defaultCategories.add(drinks)
        defaultCategories.add(fastFood)
        defaultCategories.add(packedFood)

        return defaultCategories
    }
}