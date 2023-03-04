package com.mihir.podcast.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.mihir.podcast.adapter.SearchResultAdapter
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.ui.databinding.ActivityMainBinding
import com.mihir.podcast.ui.search.SearchResult

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: FavViewModel by lazy { ViewModelProvider(this)[FavViewModel::class.java] }
    private lateinit var myAdapter: SearchResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(binding.root)

        binding.imgSearch.setOnClickListener {
            val intent = Intent(this, SearchResult::class.java)
            startActivity(intent)
        }
        myAdapter = SearchResultAdapter(this, viewModel, true) { searchItem, pair1, pair2 ->
            val intent = Intent(this, PodcastDetails::class.java)
            intent.putExtra("Search", searchItem)
            val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2)
            startActivity(intent, transition.toBundle())
        }
        binding.recyclerview.adapter = myAdapter
        viewModel.readAllFavPodcast.observe(this) { favs ->
            myAdapter.list = favs
            binding.recyclerview.scheduleLayoutAnimation()
        }
    }
}