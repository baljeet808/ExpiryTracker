package com.baljeet.expirytracker.fragment.shared

import androidx.lifecycle.ViewModel
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product

class SelectFromViewModel: ViewModel() {

    private var expiryDate : Long? = null
    private var mfgDate : Long? = null

    private var selectedCategory : Category? =  null
    private var selectedProduct : Product? =  null
    private val categoryList  = ArrayList<Category>()
    fun getAllCategories(): ArrayList<Category>{return categoryList}
    private fun addToCategories(categories : ArrayList<Category>){categoryList.addAll(categories)}
    fun addSingleCategory(category : Category){categoryList.add(category)}
    fun removeSingleCategory(category : Category){categoryList.remove(category)}
    fun setSelectedCategory(category: Category?){selectedCategory = category}
    fun getSelectedCategory(): Category?{return selectedCategory}

    private val productList = ArrayList<Product>()
    fun setSelectedProduct (product: Product?){ selectedProduct = product }
    fun getSelectedProduct(): Product?{return selectedProduct}
    fun addProduct(product : Product){productList.add(product)}
    fun removeProduct(product: Product){productList.remove(product)}
    fun getProducts(): ArrayList<Product>{return productList}
    fun getProductsByCategory():ArrayList<Product>{
        productList.clear()

                productList.addAll(getFruits())
                productList.addAll(getVegetables())

        return productList
    }
    init {
        addToCategories(getDefaultCategories())
        productList.addAll(getFruits())
    }

    private fun getImages():ArrayList<Image>{
        val images = ArrayList<Image>()
        images.add(Image(
            0,
            "fruits",
            "fruits",
            "fruits"
        ))
        images.add(Image(
            0,
            "vegetable",
            "vegetables",
            "vegetables"
        ))
        images.add(Image(
            0,
            "meat",
            "meat",
            "meat"
        ))
        images.add(Image(
            0,
            "document",
            "document",
            "document"
        ))
        images.add(Image(
            0,
            "subscription",
            "subscription",
            "subscription"
        ))
        images.add(Image(
            0,
            "packed_food",
            "packed_food",
            "packed_food"
        ))
        images.add(Image(
            0,
            "liquor",
            "liquor",
            "liquor"
        ))
        images.add(Image(
            0,
            "drinks",
            "drinks",
            "drinks"
        ))
        images.add(Image(
            0,
            "fast_food",
            "fast_food",
            "fast_food"
        ))

        return images
    }

    private fun getDefaultCategories(): ArrayList<Category>{
        val defaultCategories = ArrayList<Category>()

        val fruits = Category(
            0,
            "Fruit",
            0
        )
        val vegetables = Category(
            0,
            "Vegetable",
            0
        )
        val meat = Category(
            0,
            "Meat",
            0
        )
        val document = Category(
            0,
            "Document",
            0
        )
        val subscription = Category(
            0,
            "Subscription",
            0
        )
        val packedFood = Category(
            0,
            "Packed Food",
            0
        )
        val liquor = Category(
            0,
            "Liquor",
            0
        )
        val drinks = Category(
            0,
            "Drinkable",
            0
        )
        val fastFood = Category(
            0,
            "Fast Food",
            0
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

    private fun getVegetables(): ArrayList<Product>{
        val products = ArrayList<Product>()
        val broccoli = Product(
            0,
            "Broccoli",
            0,0
        )
        val potato = Product(
            0,
            "Potato",
            0,0
        )
        val peas = Product(
            0,
            "Peas",
            0,0
        )
        val bellPepper = Product(
            0,
            "Bell Pepper",
            0,0
        )
        val tomato = Product(
            0,
            "Tomato",
            0,0
        )


        products.add(broccoli)
        products.add(potato)
        products.add(bellPepper)
        products.add(peas)
        products.add(tomato)
        return products
    }

    private fun getFruits(): ArrayList<Product>{
        val products = ArrayList<Product>()

        val banana = Product(
            0,
            "Banana",
            0,
            0
        )
        val pineApple = Product(
            0,
            "Pineapple",
            0,
            0
        )
        val grapes = Product(
            0,
            "Grapes",
            0,
            0
        )
        val orange = Product(
            0,
            "Orange",
            0,
            0
        )
        val apple = Product(
            0,
            "Apple",
            0,
            0
        )
        val redGrapes = Product(
            0,
            "Red Grapes",
            0,0
        )

        products.add(redGrapes)
        products.add(pineApple)
        products.add(apple)
        products.add(orange)
        products.add(grapes)
        products.add(banana)


        return products
    }

    fun setExpiryDate(its: Long?) {
            expiryDate = its
    }
    fun getExpiryDate():Long?{
        return expiryDate
    }

    fun setMfgDate(its: Long?) {
        mfgDate = its
    }
    fun getMfgDate():Long?{
        return mfgDate
    }
}