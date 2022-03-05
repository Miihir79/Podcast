package com.mihir.podcast.ui

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mihir.podcast.adapter.SearchResultAdapter
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.remote.ItunesGet
import com.mihir.podcast.ui.databinding.ActivitySearchResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchResult : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val itunes: ItunesGet = ItunesGet.instance
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                GlobalScope.launch(Dispatchers.Main) {
                    if (p0 != null) {
                        binding.searchView.clearFocus()
                        binding.txtGreeting.visibility = View.GONE
                        call(itunes,p0)
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

    }

    private suspend fun call(itunes: ItunesGet,term:String){
        try{
            val result = itunes.search(term)
            val podcasts = result.body()?.results
            val podcastList = podcasts?.map {
                SearchClass(it.collectionCensoredName,it.releaseDate,it.artworkUrl600,it.feedUrl)
            }
            binding.rvSearch.adapter = SearchResultAdapter(podcastList as ArrayList<SearchClass>)
            binding.rvSearch.layoutManager = LinearLayoutManager(this)
        }catch (e: Exception){
            Log.e("TAG-mihir", "call: $e")
        }

    }
}