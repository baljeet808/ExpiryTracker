package com.baljeet.expirytracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.baljeet.expirytracker.data.daos.CategoryDao
import com.baljeet.expirytracker.data.daos.ImageDao
import com.baljeet.expirytracker.data.daos.ProductsDao
import com.baljeet.expirytracker.data.daos.TrackerDao

@Database(entities = [Product::class,Category::class,Image::class,Tracker::class],version =1 , exportSchema = false)
abstract class AppDatabase :RoomDatabase(){
    abstract fun productDao(): ProductsDao
    abstract fun imageDao(): ImageDao
    abstract fun categoryDao(): CategoryDao
    abstract fun trackerDao():TrackerDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context):AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}