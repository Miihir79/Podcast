package com.mihir.podcast.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mihir.podcast.adapter.EpisodesAdapter
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.remote.RssFeedService
import com.mihir.podcast.ui.databinding.ActivityPodcastDetailsBinding
import kotlinx.coroutines.*

class PodcastDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPodcastDetailsBinding
    private lateinit var imgUrl:String
    private lateinit var response:RssFeedResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastDetailsBinding.inflate(layoutInflater)
        with(window) {
            statusBarColor = ContextCompat.getColor(context,R.color.gray)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            allowReturnTransitionOverlap
            allowEnterTransitionOverlap
        }
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.progressBar2.visibility = View.GONE
        val intent = intent.getSerializableExtra("Search") as SearchClass
        val title = intent.name
        imgUrl = intent.imageUrl.toString()
        val feedUrl = intent.feedUrl
        Glide.with(this).load(imgUrl).into(binding.imgVPodcastImg)
        binding.txtPodcastTitle.text = title
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            val temp = podcastDetails(feedUrl.toString())
            if (temp != null) {
                binding.progressBar2.visibility = View.GONE
                response = temp
                binding.txtDetail.text = response.description
                binding.rVEpisodes.adapter = EpisodesAdapter(response,imgUrl,binding.imgVPodcastImg,this@PodcastDetails,binding.txtPodcastTitle)
            }
        }
    }

    private suspend fun podcastDetails(feedUrl: String): RssFeedResponse? = withContext(Dispatchers.IO) {
        return@withContext RssFeedService.instance.getFeed(feedUrl)
    }

    override fun onDestroy() {
        window.allowReturnTransitionOverlap
        super.onDestroy()
    }
}