package com.mihir.podcast.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mihir.podcast.adapter.SearchResultAdapter
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.PodcastDetails
import com.mihir.podcast.ui.R
import com.mihir.podcast.ui.databinding.ActivitySearchResultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResult : AppCompatActivity() {
    private val binding by lazy { ActivitySearchResultBinding.inflate(layoutInflater) }
    private val viewModel: FavViewModel by lazy { ViewModelProvider(this)[FavViewModel::class.java] }
    private val viewModelSearch: SearchResultViewModel by lazy { ViewModelProvider(this)[SearchResultViewModel::class.java] }

    private lateinit var myAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            statusBarColor = ContextCompat.getColor(context, R.color.gray)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            allowEnterTransitionOverlap
            allowReturnTransitionOverlap
        }
        supportActionBar?.hide()
        setContentView(binding.root)
        binding.searchView.isIconified = false // to open keyboard in search view

        myAdapter = SearchResultAdapter(this, viewModel, false) { searchItem, pair1, pair2 ->
            val intent = Intent(this, PodcastDetails::class.java)
            intent.putExtra("Search", searchItem)
            val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2)
            startActivity(intent, transition.toBundle())
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                CoroutineScope(Dispatchers.Main).launch {
                    if (text != null) {
                        binding.progressSearch.visibility = View.VISIBLE
                        viewModelSearch.searchPodcast(text)
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

        viewModelSearch.searchResultData.observe(this) {
            val podcastList = it.map { Podcast ->
                SearchClass(0, Podcast.collectionCensoredName, Podcast.releaseDate, Podcast.artworkUrl600, Podcast.feedUrl)
            }
            binding.progressSearch.visibility = View.GONE
            binding.rvSearch.adapter = myAdapter
            myAdapter.list = podcastList
            binding.rvSearch.scheduleLayoutAnimation()
        }
    }
}