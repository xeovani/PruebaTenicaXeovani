package com.example.pruebatecnicaxeovani.dataModel.entitiesLocation

import androidx.room.Entity

@Entity(tableName = "Location")
data class Location(
    val latitude:String,
    val longitude:String
    )
