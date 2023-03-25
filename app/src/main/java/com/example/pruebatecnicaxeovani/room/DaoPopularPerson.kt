package com.example.pruebatecnicaxeovani.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pruebatecnicaxeovani.responses2.KnownFor


@Dao
interface DaoPopularPerson {
    //Hacemos la insercion
    @Insert
    suspend fun insertPerson(mutableList: MutableList<KnownFor>)

    //Seleccionados la lista
    @Query("SELECT * FROM KnownFors")
    suspend fun getPopularPerson():List<KnownFor>



}