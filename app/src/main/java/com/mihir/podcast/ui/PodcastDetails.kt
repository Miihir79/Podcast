package com.mihir.podcast.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mihir.podcast.adapter.EpisodesAdapter
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.remote.RssFeedService
import com.mihir.podcast.ui.databinding.ActivityPodcastDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPodcastDetailsBinding
    private lateinit var ImgUrl:String
    private lateinit var response:RssFeedResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastDetailsBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this,R.color.gray)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            window.allowReturnTransitionOverlap
            window.allowEnterTransitionOverlap
        }
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.progressBar2.visibility = View.GONE
        val intent = intent.getSerializableExtra("Search") as SearchClass
        val title = intent.name
        ImgUrl = intent.imageUrl.toString()
        val feedUrl = intent.feedUrl
        Glide.with(this).load(ImgUrl).into(binding.imgVPodcastImg)
        binding.txtPodcastTitle.text = title
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar2.visibility = View.VISIBLE
            val temp = podcastDetails(feedUrl.toString())
            if (temp != null) {
                binding.progressBar2.visibility = View.GONE
                response = temp
                binding.txtDetail.text = response.description
                binding.rVEpisodes.adapter = EpisodesAdapter(response,ImgUrl,binding.imgVPodcastImg,this@PodcastDetails,binding.txtPodcastTitle)
                binding.rVEpisodes.layoutManager = LinearLayoutManager(this@PodcastDetails)
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