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

    fun getImages():ArrayList<Image>{
        val images = ArrayList<Image>()
        images.add(Image(
            1,
            "fruits",
            "fruits",
            "fruits"
        ))
        images.add(Image(
            2,
            "vegetable",
            "vegetables",
            "vegetables"
        ))
        images.add(Image(
            3,
            "meat",
            "meat",
            "meat"
        ))
        images.add(Image(
            4,
            "document",
            "document",
            "document"
        ))
        images.add(Image(
            5,
            "subscription",
            "subscription",
            "subscription"
        ))
        images.add(Image(
            6,
            "packed_food",
            "packed_food",
            "packed_food"
        ))
        images.add(Image(
            7,
            "liquor",
            "liquor",
            "liquor"
        ))
        images.add(Image(
            8,
            "drinks",
            "drinks",
            "drinks"
        ))
        images.add(Image(
            9,
            "fast_food",
            "fast_food",
            "fast_food"
        ))
        images.add(Image(
            10,
            "apple",
            "apple",
            "Apple"
        ))
        images.add(Image(
            11,
            "banana",
            "banana",
            "Banana"
        ))
        images.add(Image(
            12,
            "grapes",
            "grapes",
            "Grapes"
        ))
        images.add(Image(
            13,
            "orange",
            "orange",
            "Orange"
        ))
        images.add(Image(
            14,
            "pineapple",
            "pineapple",
            "Pineapple"
        ))
        images.add(Image(
            15,
            "red_grapes",
            "red_grapes",
            "Red Grapes"
        ))
        images.add(Image(
            16,
            "potato",
            "potato",
            "Potato"
        ))
        images.add(Image(
            17,
            "peas",
            "peas",
            "Peas"
        ))
        images.add(Image(
            18,
            "broccoli",
            "broccoli",
            "Broccoli"
        ))
        images.add(Image(
            19,
            "bell_yellow_pepper",
            "bell_yellow_pepper",
            "bell yellow pepper"
        ))
        images.add(Image(
            20,
            "tomato",
            "tomato",
            "Tomato"
        ))
        images.add(Image(
            21,
            "strawberries",
            "strawberries",
            "Strawberries"
        ))


        return images
    }

    private fun getDefaultCategories(): ArrayList<Category>{
        val defaultCategories = ArrayList<Category>()

        val fruits = Category(
            0,
            "Fruit",
            1
        )
        val vegetables = Category(
            0,
            "Vegetable",
            2
        )
        val meat = Category(
            0,
            "Meat",
            3
        )
        val document = Category(
            0,
            "Document",
            4
        )
        val subscription = Category(
            0,
            "Subscription",
            5
        )
        val packedFood = Category(
            0,
            "Packed Food",
            6
        )
        val liquor = Category(
            0,
            "Liquor",
            7
        )
        val drinks = Category(
            0,
            "Drinkable",
            8
        )
        val fastFood = Category(
            0,
            "Fast Food",
            9
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

    fun getAllProducts():ArrayList<Product>{
        val products = ArrayList<Product>()
        products.addAll(getFruits())
        products.addAll(getVegetables())

        return products
    }

    private fun getVegetables(): ArrayList<Product>{
        val products = ArrayList<Product>()
        val broccoli = Product(
            0,
            "Broccoli",
            2,0
        )
        val potato = Product(
            0,
            "Potato",
            2,0
        )
        val peas = Product(
            0,
            "Peas",
            2,0
        )
        val bellPepper = Product(
            0,
            "Bell Pepper",
            2,0
        )
        val tomato = Product(
            0,
            "Tomato",
            2,0
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
            1,
            0
        )
        val pineApple = Product(
            0,
            "Pineapple",
            1,
            0
        )
        val grapes = Product(
            0,
            "Grapes",
            1,
            0
        )
        val orange = Product(
            0,
            "Orange",
            1,
            0
        )
        val apple = Product(
            0,
            "Apple",
            1,
            0
        )
        val redGrapes = Product(
            0,
            "Red Grapes",
            1,0
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