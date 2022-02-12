package com.baljeet.expirytracker.fragment.shared

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.Image

class IconsViewModel(app : Application) : AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()

    var iconsResults = MutableLiveData<ArrayList<Image>>(ArrayList())

    var selectedIcon : Image? = null

    private val allImages = getImages()

    fun getAllIcons(){
        iconsResults.value = allImages
    }

    fun getIconByName(name : String){
        iconsResults.value = allImages.filter { i -> i.imageName.contains(name, true) }.toCollection(ArrayList())
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
                "bell yellow pepper"
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

        return images
    }


}