package com.example.pruebatecnicaxeovani.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pruebatecnicaxeovani.dataModel.entitiesLocation.Location

@Dao
interface DaoLastLocation {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastLocation(mutableList:MutableList<Location>)

    @Query("SELECT * FROM location")
    suspend fun getLastLocation():List<Location>
}