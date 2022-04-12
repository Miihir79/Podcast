package com.mihir.podcast.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchClass::class],version = 1,exportSchema = false)
abstract class FavDatabase: RoomDatabase() {

    abstract fun FavDao():FavInterface

    companion object{
        @Volatile
        private var INSTANCE: FavDatabase?=null

        fun getDatabase(context: Context):FavDatabase{
            val tempInstance = INSTANCE
            if (tempInstance!= null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDatabase::class.java,
                    "fav_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}