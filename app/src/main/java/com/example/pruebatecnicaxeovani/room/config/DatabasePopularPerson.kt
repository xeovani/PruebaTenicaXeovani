package com.example.pruebatecnicaxeovani.room.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pruebatecnicaxeovani.responses2.KnownFor
import com.example.pruebatecnicaxeovani.room.DaoPopularPerson
@Database(entities = [KnownFor::class], version = 1, exportSchema = false)
abstract class DatabasePopularPerson: RoomDatabase (){

    abstract fun daoPopularPerson(): DaoPopularPerson

    companion object{
        @Volatile
        private var INSTANCE: DatabasePopularPerson? = null

        fun getDatabase(context: Context): DatabasePopularPerson{


            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    DatabasePopularPerson::class.java,
                    "DatabasePopularPerson")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }

        }

    }
}