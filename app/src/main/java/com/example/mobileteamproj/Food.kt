package com.example.mobileteamproj

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="food")
class Food (
    @PrimaryKey(autoGenerate = true) var id:Long?,
    @ColumnInfo(name="foodurl") var foodUrl:String,
    @ColumnInfo(name="eatplace") var eatPlace:String,
    @ColumnInfo(name="foodname") var foodName:String,
    @ColumnInfo(name="foodnum") var foodNum:Int,
    @ColumnInfo(name="eattime") var eatTime:String,
    @ColumnInfo(name="foodkcal") var foodKcal:Int,
    @ColumnInfo(name="foodcomment") var foodComment:String,
    )
{
    constructor():this(null,"","","",0,"",0,"")

}