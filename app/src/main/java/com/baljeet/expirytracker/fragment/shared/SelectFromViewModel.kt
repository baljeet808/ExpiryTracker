package com.baljeet.expirytracker.fragment.shared

import androidx.lifecycle.ViewModel
import com.baljeet.expirytracker.model.Category
import com.baljeet.expirytracker.model.Image
import com.baljeet.expirytracker.model.Product

class SelectFromViewModel: ViewModel() {


    private var selectedCategory :Category? =  null
    private var selectedProduct :Product? =  null
    private val categoryList  = ArrayList<Category>()
    fun getAllCategories(): ArrayList<Category>{return categoryList}
    private fun addToCategories(categories : ArrayList<Category>){categoryList.addAll(categories)}
    fun addSingleCategory(category : Category){categoryList.add(category)}
    fun removeSingleCategory(category : Category){categoryList.remove(category)}
    fun setSelectedCategory(category: Category?){selectedCategory = category}
    fun getSelectedCategory(): Category?{return selectedCategory}

    private val productList = ArrayList<Product>()
    fun setSelectedProduct (product: Product?){ selectedProduct = product }
    fun getSelectedProduct():Product?{return selectedProduct}
    fun addProduct(product : Product){productList.add(product)}
    fun removeProduct(product: Product){productList.remove(product)}
    fun getProducts(): ArrayList<Product>{return productList}
    init {
        addToCategories(getDefaultCategories())
        productList.addAll(getFruits())
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

    private fun getFruits(): ArrayList<Product>{
        val products = ArrayList<Product>()

        val banana = Product(
            1,
            "Banana",
            1,
            Image(
                1,
                "banana",
                "banana",
                "banana"
            )
        )
        val pineApple = Product(
            1,
            "Pineapple",
            1,
            Image(
                1,
                "pineapple",
                "pineapple",
                "pineapple"
            )
        )
        val grapes = Product(
            1,
            "Grapes",
            1,
            Image(
                1,
                "grapes",
                "grapes",
                "grapes"
            )
        )
        val orange = Product(
            1,
            "Orange",
            1,
            Image(
                1,
                "orange",
                "orange",
                "orange"
            )
        )
        val apple = Product(
            1,
            "Apple",
            1,
            Image(
                1,
                "apple",
                "apple",
                "apple"
            )
        )
        val redGrapes = Product(
            1,
            "Red Grapes",
            1,
            Image(
                1,
                "red_grapes",
                "red_grapes",
                "red grapes"
            )
        )

        products.add(redGrapes)
        products.add(pineApple)
        products.add(apple)
        products.add(orange)
        products.add(grapes)
        products.add(banana)


        return products
    }
}