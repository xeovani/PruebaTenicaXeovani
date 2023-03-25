package com.example.pruebatecnicaxeovani.room.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pruebatecnicaxeovani.room.DaoPopularMovies
@Database(entities = [com.example.pruebatecnicaxeovani.responses2.movies.Result::class], version = 2, exportSchema = false)
abstract class DatabasePopularMovies : RoomDatabase(){

    abstract fun daoPopularMovies(): DaoPopularMovies

    companion object{
        @Volatile
        private var INSTANCE: DatabasePopularMovies? = null


        fun getDatabasePopularMovies(context: Context):DatabasePopularMovies{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    DatabasePopularMovies::class.java,
                    "DatabasePopularMovies")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance

            }
        }
    }
}