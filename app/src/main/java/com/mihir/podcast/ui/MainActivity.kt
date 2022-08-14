package com.mihir.podcast.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.mihir.podcast.adapter.SearchResultAdapter
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.databinding.ActivityMainBinding
import com.mihir.podcast.ui.search.SearchResult

class MainActivity : AppCompatActivity() {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val viewModel:FavViewModel by lazy { ViewModelProvider(this)[FavViewModel::class.java] }
    private val myAdapter by lazy { SearchResultAdapter(this,viewModel,true)}
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.gray)
        setContentView(binding.root)

        binding.imgSearch.setOnClickListener {
            val intent = Intent(this, SearchResult::class.java)
            startActivity(intent)
        }

        binding.recyclerview.adapter = myAdapter
        viewModel.readAllFavPodcast.observe(this) { favs ->
            myAdapter.setList(favs as ArrayList<SearchClass>)
            binding.recyclerview.scheduleLayoutAnimation()
        }
    }
}