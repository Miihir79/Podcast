package com.mihir.podcast.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import coil.load
import com.mihir.podcast.INTENT_KEY_EPISODE
import com.mihir.podcast.INTENT_KEY_NAME
import com.mihir.podcast.INTENT_KEY_SEARCH
import com.mihir.podcast.INTENT_KEY_URL
import com.mihir.podcast.adapter.EpisodesAdapter
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.remote.RssFeedService
import com.mihir.podcast.ui.databinding.ActivityPodcastDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetails : AppCompatActivity() {

    private val binding by lazy { ActivityPodcastDetailsBinding.inflate(layoutInflater) }
    private lateinit var imgUrl: String
    private lateinit var response: RssFeedResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            statusBarColor = ContextCompat.getColor(context, R.color.gray)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            allowReturnTransitionOverlap
            allowEnterTransitionOverlap
        }
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.progressBar2.visibility = View.GONE
        val intent = intent.getSerializableExtra(INTENT_KEY_SEARCH) as SearchClass
        val title = intent.name
        imgUrl = intent.imageUrl.toString()
        val feedUrl = intent.feedUrl
        binding.imgVPodcastImg.load(imgUrl)
        binding.txtPodcastTitle.text = title

        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            val temp = podcastDetails(feedUrl.toString())
            if (temp != null) {
                binding.progressBar2.visibility = View.GONE
                response = temp
                binding.txtDetail.text = response.description
                binding.rVEpisodes.adapter =
                    EpisodesAdapter(response) { episode, title, desc ->
                        val intentTransition = Intent(this@PodcastDetails, Player::class.java)
                        intentTransition.putExtra(INTENT_KEY_NAME, response.title)
                        intentTransition.putExtra(INTENT_KEY_EPISODE, episode)
                        intentTransition.putExtra(INTENT_KEY_URL, imgUrl)
                        val pair1 = Pair.create<View, String>(binding.imgVPodcastImg, "img_small")
                        val pair2 = Pair.create<View, String>(binding.txtPodcastTitle, "title")
                        val pair3 = Pair.create<View, String>(title, "podName")
                        val pair4 = Pair.create<View, String>(desc, "description")
                        val transition =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@PodcastDetails, pair1, pair2, pair3, pair4)
                        startActivity(intentTransition, transition.toBundle())
                    }
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