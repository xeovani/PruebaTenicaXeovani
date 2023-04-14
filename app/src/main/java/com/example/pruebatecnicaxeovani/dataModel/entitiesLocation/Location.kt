package com.example.pruebatecnicaxeovani.dataModel.entitiesLocation

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Location")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val latitude:String = "",
    val longitude:String = ""
    )
