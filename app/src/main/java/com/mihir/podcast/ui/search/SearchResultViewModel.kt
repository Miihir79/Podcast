package com.mihir.podcast.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mihir.podcast.model.ResponseClass
import com.mihir.podcast.remote.ItunesGet

class SearchResultViewModel: ViewModel() {

    val searchResultData: MutableLiveData<List<ResponseClass.Podcast>> = MutableLiveData()

    suspend fun searchPodcast(itunes: ItunesGet, term:String){
        try {
            val result = itunes.search(term)
            searchResultData.postValue(result.body()?.results)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}