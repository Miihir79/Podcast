package com.mihir.podcast.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mihir.podcast.helper.HtmlUtils
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.ui.databinding.ActivityPlayerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class Player : AppCompatActivity() {
    private val binding by lazy { ActivityPlayerBinding.inflate(layoutInflater) }
    private var state: Boolean = true //(true)->paused state[image of play] ; (false)->playing state [image of pause]
    private lateinit var podcastURL: String
    private var mediaPlayer: MediaPlayer? = null
    private val load by lazy {
        CoroutineScope(Dispatchers.IO).launch {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
            kotlin.runCatching {
                mediaPlayer?.setDataSource(podcastURL)
                mediaPlayer?.prepare()
            }
        }
    }

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

        val episode = intent.getSerializableExtra("episode") as RssFeedResponse.EpisodeResponse
        val podName = intent.getStringExtra("name")
        val url = intent.getStringExtra("url")

        binding.txtPlayerEpTitle.text = episode.title
        binding.txtPlayerDescription.text = episode.description?.let { HtmlUtils.htmlToSpannable(it) }
        podcastURL = episode.url.toString()

        binding.txtPlayerTitle.text = podName
        Glide.with(binding.episodeImageView.context).load(url).into(binding.episodeImageView)
        binding.txtTotal.text = episode.duration

        binding.imgPlay.setOnClickListener {
            if (state) {
                play()
            } else {
                pause()
            }
        }
        binding.txtSpeed.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (load.isCompleted) { // just in case the prepare() is not executed
                    Log.i("TAG", "onCreate: clicked")
                    val params = mediaPlayer?.playbackParams
                    params?.speed = 2f
                    if (params != null) {
                        mediaPlayer?.playbackParams = params
                    }
                }
            }
        }
        // to enable seeking of audio from seekbar
        binding.seekBarEpisode.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2 && load.isCompleted) {
                    mediaPlayer!!.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i("TAG", "onStartTrackingTouch: ")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.i("TAG", "onStopTrackingTouch: ")
            }

        })

        binding.imgPrev.setOnClickListener {
            if (load.isCompleted) {
                val newTime = mediaPlayer!!.currentPosition - 5000
                if (newTime > 0) {
                    mediaPlayer!!.seekTo(newTime)
                } else {
                    mediaPlayer!!.seekTo(0)
                }
            }
        }
        binding.imgNext.setOnClickListener {
            if (load.isCompleted) {
                val newTime = mediaPlayer!!.currentPosition + 5000
                if (newTime < mediaPlayer!!.duration) {
                    mediaPlayer!!.seekTo(newTime)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!load.isCompleted) {
            load
        }
    }

    private fun play() {
        state = false
        binding.imgPlay.setImageResource(R.drawable.ic_baseline_pause_24)
        binding.progressBar.visibility = View.VISIBLE // to show loading till the prepare() is ready
        load.invokeOnCompletion {// wait for prepare() to be ready
            mediaPlayer?.start()
            runOnUiThread { // to update UI on the main thread
                binding.progressBar.visibility = View.GONE
                binding.seekBarEpisode.progress = mediaPlayer!!.currentPosition
                binding.seekBarEpisode.max = mediaPlayer!!.duration
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object : Runnable { // to update the values of seekbar and curr time continually
                    override fun run() {
                        try {
                            binding.seekBarEpisode.progress = mediaPlayer!!.currentPosition
                            binding.txtCurrent.text = convert(binding.seekBarEpisode.progress)
                            handler.postDelayed(this, 1000)
                            if (!mediaPlayer!!.isPlaying) {
                                binding.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_black)
                                state = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }, 0)
            }
        }

    }

    private fun pause() {
        state = true
        binding.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_black)
        mediaPlayer?.pause()
    }

    fun convert(duration: Int): String { // to convert mill sec to minutes and seconds
        return String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.pause()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBackPressed() {
        if (state) {
            mediaPlayer?.release()
            mediaPlayer = null
            super.onBackPressed()
        } else {
            MaterialAlertDialogBuilder(this).setTitle("Stop the player?")
                .setMessage("Going Back will stop the podcast, do you still want to go back?")
                .setPositiveButton("Yes") { _, _ ->
                    mediaPlayer?.release()
                    mediaPlayer = null
                    super.onBackPressed()
                }
                .setNegativeButton("No") { _, _ ->
                }
                .create()
                .show()
        }
    }
}