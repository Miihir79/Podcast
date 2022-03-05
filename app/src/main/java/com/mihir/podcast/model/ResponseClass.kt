package com.mihir.podcast.model

data class ResponseClass(
        val resultCount:Int,
        val results: List<Podcast>){
        data class Podcast(
            val collectionCensoredName: String,
            val feedUrl: String,
            val artworkUrl600: String,
            val releaseDate: String
        )
}

