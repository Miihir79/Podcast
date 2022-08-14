package com.mihir.podcast.model

import androidx.annotation.Keep

@Keep
data class ResponseClass(
        val resultCount:Int,
        val results: List<Podcast>){
        @Keep
        data class Podcast(
            val collectionCensoredName: String,
            val feedUrl: String,
            val artworkUrl600: String,
            val releaseDate: String
        )
}

