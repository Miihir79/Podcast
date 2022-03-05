package com.mihir.podcast.remote

import com.mihir.podcast.model.ResponseClass
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val BASE_URL ="https://itunes.apple.com"
interface ItunesGet {
    @GET("/search?media=podcast")
    suspend fun search(@Query("term") term:String):
            Response<ResponseClass>

    companion object{
        val instance:ItunesGet by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(ItunesGet::class.java)
        }
    }
}