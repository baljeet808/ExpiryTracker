package com.baljeet.expirytracker.fragment.shared

import androidx.lifecycle.ViewModel
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import kotlinx.datetime.LocalDate

class SelectFromViewModel: ViewModel() {

    private var expiryDate : LocalDate? = null
    private var mfgDate : LocalDate? = null

    private var selectedCategory : CategoryAndImage? =  null
    private var selectedProduct : ProductAndImage? =  null
    private val categoryList  = ArrayList<Category>()
    fun getAllCategories(): ArrayList<Category>{return categoryList}
    private fun addToCategories(categories : ArrayList<Category>){categoryList.addAll(categories)}
    fun addSingleCategory(category : Category){categoryList.add(category)}
    fun removeSingleCategory(category : Category){categoryList.remove(category)}
    fun setSelectedCategory(category: CategoryAndImage?){selectedCategory = category}
    fun getSelectedCategory(): CategoryAndImage?{return selectedCategory}

    private val productList = ArrayList<Product>()
    fun setSelectedProduct (product: ProductAndImage?){ selectedProduct = product }
    fun getSelectedProduct(): ProductAndImage?{return selectedProduct}
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

    fun getDefaultCategories(): ArrayList<Category>{
        val defaultCategories = ArrayList<Category>()

        val fruits = Category(
            1,
            "Fruit",
            1
        )
        val vegetables = Category(
            2,
            "Vegetable",
            2
        )
        val meat = Category(
            3,
            "Meat",
            3
        )
        val document = Category(
            4,
            "Document",
            4
        )
        val subscription = Category(
            5,
            "Subscription",
            5
        )
        val packedFood = Category(
            6,
            "Packed Food",
            6
        )
        val liquor = Category(
            7,
            "Liquor",
            7
        )
        val drinks = Category(
            8,
            "Drinkable",
            8
        )
        val fastFood = Category(
            9,
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
            1,
            "Broccoli",
            2,18
        )
        val potato = Product(
            2,
            "Potato",
            2,16
        )
        val peas = Product(
            3,
            "Peas",
            2,17
        )
        val bellPepper = Product(
            4,
            "Bell Pepper",
            2,19
        )
        val tomato = Product(
            5,
            "Tomato",
            2,20
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
            6,
            "Banana",
            1,
            11
        )
        val pineApple = Product(
            7,
            "Pineapple",
            1,
            14
        )
        val grapes = Product(
            8,
            "Grapes",
            1,
            12
        )
        val orange = Product(
            9,
            "Orange",
            1,
            13
        )
        val apple = Product(
            10,
            "Apple",
            1,
            10
        )
        val redGrapes = Product(
            11,
            "Red Grapes",
            1,15
        )

        products.add(redGrapes)
        products.add(pineApple)
        products.add(apple)
        products.add(orange)
        products.add(grapes)
        products.add(banana)


        return products
    }

    fun setExpiryDate(date: LocalDate) {
            expiryDate = date
    }
    fun getExpiryDate():LocalDate?{
        return expiryDate
    }

    fun setMfgDate(date: LocalDate) {
        mfgDate = date
    }
    fun getMfgDate():LocalDate?{
        return mfgDate
    }
}