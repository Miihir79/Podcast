package com.mihir.podcast.model

import java.io.Serializable

data class SearchClass(var name: String? = "",
                       var lastUpdated: String? = "",
                       var imageUrl: String? = "",
                       var feedUrl: String? = ""):Serializable
