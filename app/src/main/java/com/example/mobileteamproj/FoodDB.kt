package com.example.mobileteamproj

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[Food::class],version=1)
abstract class FoodDB:RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object{
        private var INSTANCE: FoodDB?=null

        fun getInstance(context: Context):FoodDB?{
            if(INSTANCE==null){
                synchronized(FoodDB::class){
                    INSTANCE= Room.databaseBuilder(context.applicationContext,
                    FoodDB::class.java,"food.db").fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }
        fun destroyInstance(){
            INSTANCE=null
        }
    }
}