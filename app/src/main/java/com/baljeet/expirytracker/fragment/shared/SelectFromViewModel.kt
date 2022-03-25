package com.baljeet.expirytracker.fragment.shared

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import java.time.LocalDateTime

class SelectFromViewModel : ViewModel() {

    private var expiryDate: LocalDateTime? = null
    private var mfgDate: LocalDateTime? = null
    var reminderDate: LocalDateTime? = null

    private var selectedCategory: CategoryAndImage? = null
    private var selectedProduct: ProductAndImage? = null
    private val categoryList = ArrayList<Category>()
    private fun addToCategories(categories: ArrayList<Category>) {
        categoryList.addAll(categories)
    }

    fun setSelectedCategory(category: CategoryAndImage?) {
        selectedCategory = category
    }

    fun getSelectedCategory(): CategoryAndImage? {
        return selectedCategory
    }

    private val productList = ArrayList<Product>()
    fun setSelectedProduct(product: ProductAndImage?) {
        selectedProduct = product
    }

    fun getSelectedProduct(): ProductAndImage? {
        return selectedProduct
    }

    init {
        addToCategories(getDefaultCategories())
        productList.addAll(getFruits())
    }

    fun getImages(): ArrayList<Image> {
        val images = ArrayList<Image>()
        images.add(
            Image(
                1,
                "fruits",
                "fruits",
                "fruits" ,
                mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                2,
                "vegetable",
                "vegetables",
                "vegetables"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                3,
                "meat",
                "meat",
                "meat"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                4,
                "document",
                "document",
                "document"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                5,
                "subscription",
                "subscription",
                "subscription"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                6,
                "packed_food",
                "packed_food",
                "packed_food"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                7,
                "liquor",
                "liquor",
                "liquor"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                8,
                "drinks",
                "drinks",
                "drinks"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                9,
                "fast_food",
                "fast_food",
                "fast_food"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                10,
                "apple",
                "apple",
                "Apple"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                11,
                "banana",
                "banana",
                "Banana"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                12,
                "grapes",
                "grapes",
                "Grapes"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                13,
                "orange",
                "orange",
                "Orange"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                14,
                "pineapple",
                "pineapple",
                "Pineapple"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                15,
                "red_grapes",
                "red_grapes",
                "Red Grapes"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                16,
                "potato",
                "potato",
                "Potato"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                17,
                "peas",
                "peas",
                "Peas"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                18,
                "broccoli",
                "broccoli",
                "Broccoli"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                19,
                "bell_yellow_pepper",
                "bell_yellow_pepper",
                "Bell yellow pepper"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                20,
                "tomato",
                "tomato",
                "Tomato"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                21,
                "strawberries",
                "strawberries",
                "Strawberries"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                22,
                "avocado",
                "avocado",
                "Avocado"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                23,
                "beetroot",
                "beetroot",
                "Beetroot"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                24,
                "berry",
                "berry",
                "Berry"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                25,
                "blueberry",
                "blueberry",
                "Blueberry"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                26,
                "bread",
                "bread",
                "Bread"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                27,
                "butter",
                "butter",
                "Butter"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                28,
                "cabbage",
                "cabbage",
                "Cabbage"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                29,
                "carrot",
                "carrot",
                "Carrot"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                30,
                "cauliflower",
                "cauliflower",
                "Cauliflower"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                31,
                "cheese",
                "cheese",
                "Cheese"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                32,
                "chicken",
                "chicken",
                "Chicken"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                33,
                "cilantro",
                "cilantro",
                "Cilantro"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                34,
                "cloves",
                "cloves",
                "Cloves"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                35,
                "coconut",
                "coconut",
                "Coconut"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                36,
                "corn",
                "corn",
                "Corn"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                37,
                "cucumber",
                "cucumber",
                "Cucumber"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                38,
                "dragon_fruit",
                "dragon_fruit",
                "dragon_fruit"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                39,
                "eggplant",
                "eggplant",
                "eggplant"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                40,
                "eggs",
                "eggs",
                "eggs"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                41,
                "fish",
                "fish",
                "fish"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                42,
                "green_pepper",
                "green_pepper",
                "green_pepper"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                43,
                "green_chili",
                "green_chili",
                "green_chili"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                44,
                "jalapeno",
                "jalapeno",
                "jalapeno"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                45,
                "kiwi",
                "kiwi",
                "kiwi"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                46,
                "lemon",
                "lemon",
                "lemon"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                47,
                "mango",
                "mango",
                "mango"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                48,
                "milk",
                "milk",
                "milk"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                50,
                "mushrooms",
                "mushrooms",
                "mushrooms"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                51,
                "okra",
                "okra",
                "okra"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                52,
                "onion",
                "onion",
                "onion"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                49,
                "pear",
                "pear",
                "pear"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                53,
                "pomegranate_two",
                "pomegranate_two",
                "pomegranate_two"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                54,
                "pomegranate",
                "pomegranate",
                "pomegranate"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                55,
                "pumpkin",
                "pumpkin",
                "pumpkin"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                56,
                "rosemary",
                "rosemary",
                "rosemary"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                57,
                "shrimp",
                "shrimp",
                "shrimp"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                58,
                "spinach",
                "spinach",
                "spinach"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                59,
                "strawberry",
                "strawberry",
                "strawberry"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                61,
                "sugar",
                "sugar",
                "sugar"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                62,
                "watermelon",
                "watermelon",
                "watermelon"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                63,
                "garlic",
                "garlic",
                "garlic"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                64,
                "ginger",
                "ginger",
                "ginger"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )

        return images
    }

    fun getDefaultCategories(): ArrayList<Category> {
        val defaultCategories = ArrayList<Category>()

        val fruits = Category(
            1,
            "Fruit",
            1,
            false
        )
        val vegetables = Category(
            2,
            "Vegetable",
            2,
            false
        )
        val meat = Category(
            3,
            "Meat",
            3,
            false
        )
        val document = Category(
            4,
            "Document",
            4,
            false
        )
        val subscription = Category(
            5,
            "Subscription",
            5,
            false
        )
        val packedFood = Category(
            6,
            "Packed Food",
            6,
            false
        )
        val liquor = Category(
            7,
            "Liquor",
            7,
            false
        )
        val drinks = Category(
            8,
            "Drinkable",
            8,
            false
        )
        val fastFood = Category(
            9,
            "Fast Food",
            9,
            false
        )
        defaultCategories.add(
            Category(
                11,
                "Dairy",
                31,
                false
            )
        )
        defaultCategories.add(
            Category(
                12,
                "Bakery",
                26,
                false
            )
        )
        defaultCategories.add(
            Category(
                13,
                "Herbs and Spices",
                34,
                false
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

    fun getAllProducts(): ArrayList<Product> {
        val products = ArrayList<Product>()
        products.addAll(getFruits())
        products.addAll(getVegetables())
        products.addAll(getDairy())
        products.addAll(getMeats())
        products.addAll(getSpices())
        products.addAll(getBakery())
        return products
    }

    private fun getDairy(): ArrayList<Product>{
        val products = ArrayList<Product>()
        products.add(Product(
            36,
            "Cheese",
            11,31,
            false
        ))

        products.add(Product(
            37,
            "Butter",
            11,27,
            false
        ))

        products.add(Product(
            38,
            "Milk",
            11,48,
            false
        ))

        products.add(Product(
            39,
            "Eggs",
            11,40,
            false
        ))
        return products
    }

    private fun getMeats():ArrayList<Product>{
        val products = ArrayList<Product>()
        products.add(Product(
            40,
            "Fish",
            3,41,
            false
        ))

        products.add(Product(
            41,
            "Chicken",
            3,32,
            false
        ))

        products.add(Product(
            42,
            "Shrimp",
            3,57,
            false
        ))
        return products
    }

    private fun getVegetables(): ArrayList<Product> {
        val products = ArrayList<Product>()
        val broccoli = Product(
            1,
            "Broccoli",
            2, 18,
            false
        )
        val potato = Product(
            2,
            "Potato",
            2, 16,
            false
        )
        val peas = Product(
            3,
            "Peas",
            2, 17 ,
            false
        )
        val bellPepper = Product(
            4,
            "Bell Pepper",
            2, 19,
            false
        )
        val tomato = Product(
            5,
            "Tomato",
            2, 20,
            false
        )
        products.add(Product(
            12,
            "Avocado",
            2,22,
            false
        ))
        products.add(Product(
            13,
            "Beetroot",
            2,23,
            false
        ))
        products.add(Product(
            14,
            "Cabbage",
            2,28,
            false
        ))
        products.add(Product(
            15,
            "Carrot",
            2,29,
            false
        ))
        products.add(Product(
            16,
            "Cauliflower",
            2,30,
            false
        ))
        products.add(Product(
            17,
            "Coconut",
            2,35,
            false
        ))
        products.add(Product(
            18,
            "Corn",
            2,36,
            false
        ))
        products.add(Product(
            19,
            "Cucumber",
            2,37,
            false
        ))
        products.add(Product(
            20,
            "Eggplant",
            2,39,
            false
        ))
        products.add(Product(
            21,
            "Green pepper",
            2,42,
            false
        ))
        products.add(Product(
            22,
            "Mushrooms",
            2,50,
            false
        ))
        products.add(Product(
            23,
            "Okra",
            2,51,
            false
        ))
        products.add(Product(
            24,
            "Onion",
            2,52,
            false
        ))
        products.add(Product(
            25,
            "Pumpkin",
            2,55,
            false
        ))
        products.add(Product(
            26,
            "Spinach",
            2,58,
            false
        ))
        products.add(Product(
            43,
            "Ginger",
            2,64,
            false
        ))
        products.add(Product(
            44,
            "Garlic",
            2,63,
            false
        ))


        products.add(broccoli)
        products.add(potato)
        products.add(bellPepper)
        products.add(peas)
        products.add(tomato)
        return products
    }

    private fun getSpices():ArrayList<Product>{
        val products = ArrayList<Product>()
        products.add(Product(
            45,
            "Cloves",
            13,34,
            false
        ))
        products.add(Product(
            46,
            "Cilantro",
            13,33,
            false
        ))
        products.add(Product(
            47,
            "Green Chilli",
            13,43,
            false
        ))
        products.add(Product(
            48,
            "Jalapeno",
            13,44,
            false
        ))
        products.add(Product(
            49,
            "Rosemary",
            13,56,
            false
        ))
        return products
    }

    private fun getFruits(): ArrayList<Product> {
        val products = ArrayList<Product>()

        val banana = Product(
            6,
            "Banana",
            1,
            11,
            false
        )
        val pineApple = Product(
            7,
            "Pineapple",
            1,
            14,
            false
        )
        val grapes = Product(
            8,
            "Grapes",
            1,
            12,
            false
        )
        val orange = Product(
            9,
            "Orange",
            1,
            13,
            false
        )
        val apple = Product(
            10,
            "Apple",
            1,
            10,
            false
        )
        val redGrapes = Product(
            11,
            "Red Grapes",
            1, 15,
            false
        )
        products.add(
            Product(
            27,
            "Berries",
            1,   24,
            false
        )
        )
        products.add(Product(
            28,
            "Blueberry",
            1,   25,
            false
        ))
        products.add(Product(
            29,
            "Dragon fruit",
            1,   38,
            false
        ))
        products.add(Product(
            30,
            "Kiwi",
            1,   45,
            false
        ))
        products.add(Product(
            31,
            "Mango",
            1,   47,
            false
        ))
        products.add(Product(
            32,
            "Pear",
            1,   49,
            false
        ))
        products.add(Product(
            33,
            "Pomegranate",
            1,   53,
            false
        ))
        products.add(Product(
            34,
            "Strawberry",
            1,   59,
            false
        ))
        products.add(Product(
            35,
            "Watermelon",
            1,   62,
            false
        ))

        products.add(redGrapes)
        products.add(pineApple)
        products.add(apple)
        products.add(orange)
        products.add(grapes)
        products.add(banana)


        return products
    }

    private fun getBakery():ArrayList<Product>{
        val products = ArrayList<Product>()

        products.add(Product(
            50,
            "Breads",
            12,26,
            false
        ))
        return products
    }

    fun setExpiryDate(date: LocalDateTime) {
        expiryDate = date
    }

    fun getExpiryDate(): LocalDateTime? {
        return expiryDate
    }

    fun setMfgDate(date: LocalDateTime) {
        mfgDate = date
    }

    fun getMfgDate(): LocalDateTime? {
        return mfgDate
    }
}