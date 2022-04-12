package com.mihir.podcast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "fav_table")
data class SearchClass(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var name: String? = "",
                       var lastUpdated: String? = "",
                       var imageUrl: String? = "",
                       var feedUrl: String? = ""):Serializable
