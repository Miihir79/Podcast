package com.mihir.podcast.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavViewModel(application: Application) : AndroidViewModel(application) {
    var dao = FavDatabase.getDatabase(application).favDao()
    val readAllFavPodcast: LiveData<List<SearchClass>> = dao.getFavs()

    fun addFav(searchClass: SearchClass) {
        viewModelScope.launch {
            dao.addFav(searchClass)
        }
    }

    fun deleteFav(searchClass: SearchClass) {
        viewModelScope.launch {
            dao.deleteFav(searchClass)
        }
    }
}