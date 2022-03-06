package com.mihir.podcast.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mihir.podcast.adapter.EpisodesAdapter
import com.mihir.podcast.remote.RssFeedService
import com.mihir.podcast.ui.databinding.ActivityPodcastDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPodcastDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val title = intent.extras?.get("Title")
        val imgUrl = intent.extras?.get("Image")
        val feedUrl = intent.extras?.get("Feed")
        Glide.with(this).load(imgUrl).into(binding.imgVPodcastImg)
        binding.txtPodcastTitle.text = title as CharSequence?
        GlobalScope.launch(Dispatchers.Main) {
            podcastDetails(feedUrl.toString())
        }

    }
    suspend fun podcastDetails(feedUrl:String){
        val response = RssFeedService.instance.getFeed(feedUrl)
        binding.txtDetail.text = response?.description

        binding.rVEpisodes.adapter = response?.let { EpisodesAdapter(it) }
        binding.rVEpisodes.layoutManager = LinearLayoutManager(this)
    }
}