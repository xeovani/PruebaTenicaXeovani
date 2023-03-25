package com.example.pruebatecnicaxeovani.room.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pruebatecnicaxeovani.dataModel.entitiesLocation.Location
import com.example.pruebatecnicaxeovani.room.DaoLastLocation

@Database(entities = [Location::class], version = 3, exportSchema = false)
abstract class DatabaseLocation: RoomDatabase() {
    abstract fun daoLocation(): DaoLastLocation

    companion object{
        private var INTANCE_LOCATION: DatabaseLocation? = null

        fun getDatabaseLastLocation(context: Context):DatabaseLocation{
            return INTANCE_LOCATION ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    DatabaseLocation::class.java,
                    "DatabaseLocation")
                    .fallbackToDestructiveMigration()
                    .build()
                INTANCE_LOCATION = instance
                return instance
            }

        }

    }
}