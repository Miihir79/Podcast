package com.mihir.podcast.model

import java.util.*

data class SubscriptionClass (
        val id:Long,
        val imgUrl:String="",
        var title:String="",
        var date:Date= Date()

)