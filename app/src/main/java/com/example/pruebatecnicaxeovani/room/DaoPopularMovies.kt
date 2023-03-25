package com.example.pruebatecnicaxeovani.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoPopularMovies {
    @Insert
    suspend fun insertPopularMovies(mutableList:MutableList<com.example.pruebatecnicaxeovani.responses2.movies.Result>)

    @Query("SELECT * FROM ResultPopularMovies")
    suspend fun getPopularMovies():List<com.example.pruebatecnicaxeovani.responses2.movies.Result>
}