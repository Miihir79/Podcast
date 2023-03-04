package com.mihir.podcast.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavInterface {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFav(searchClass: SearchClass)

    @Query("SELECT * FROM fav_table")
    fun getFavs(): LiveData<List<SearchClass>>

    @Delete
    suspend fun deleteFav(searchClass: SearchClass)
}