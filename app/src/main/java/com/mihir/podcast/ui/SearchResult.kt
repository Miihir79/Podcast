package com.mihir.podcast.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mihir.podcast.adapter.SearchResultAdapter
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.remote.ItunesGet
import com.mihir.podcast.ui.databinding.ActivitySearchResultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchResult : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    private val viewModel: FavViewModel by lazy { ViewModelProvider(this)[FavViewModel::class.java] }
    private lateinit var myAdapter: SearchResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        with(window) {
            statusBarColor = ContextCompat.getColor(context,R.color.gray)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            allowEnterTransitionOverlap
            allowReturnTransitionOverlap
        }
        supportActionBar?.hide()
        setContentView(binding.root)
        binding.searchView.isIconified = false // to open keyboard in search view

        val itunes: ItunesGet = ItunesGet.instance
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        binding.progressSearch.visibility = View.VISIBLE
                        call(itunes,p0)
                        binding.searchView.clearFocus()
                        binding.txtGreeting.visibility = View.GONE
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
                SearchClass(0,it.collectionCensoredName,it.releaseDate,it.artworkUrl600,it.feedUrl)
            }
            binding.progressSearch.visibility = View.GONE
            myAdapter = SearchResultAdapter(this,viewModel,false)
            binding.rvSearch.adapter = myAdapter
            myAdapter.setList(podcastList as ArrayList<SearchClass>)
            binding.rvSearch.scheduleLayoutAnimation()
        }catch (e: Exception){
            Log.e("TAG-mihir", "call: $e")
        }

    }
}