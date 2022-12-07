package com.example.mobileteamproj

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FoodDao {
    @Query("SELECT * FROM food")
    fun getAll():List<Food>

    @Insert(onConflict=REPLACE)
    fun insert(food: Food)

    @Query("DELETE from food")
    fun deleteAll()

}