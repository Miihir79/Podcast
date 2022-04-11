package com.mihir.podcast.ui

import android.content.DialogInterface
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mihir.podcast.helper.HtmlUtils
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.ui.databinding.ActivityPlayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.concurrent.TimeUnit

class Player : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var State:Boolean=true //(true)->paused state[image of play] ; (false)->playing state [image of pause]
    private lateinit var PodcastURL :String
    private var mediaPlayer : MediaPlayer?=null
    private lateinit var load:Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this,R.color.gray)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            window.allowReturnTransitionOverlap

        }
        supportActionBar?.hide()
        setContentView(binding.root)

        val episode = intent.getSerializableExtra("episode") as RssFeedResponse.EpisodeResponse
        val podName = intent.getStringExtra("name")
        val url = intent.getStringExtra("url")

        binding.txtPlayerEpTitle.text = episode.title
        binding.txtPlayerDescription.text = episode.description?.let { HtmlUtils.htmlToSpannable(it) }
        PodcastURL = episode.url.toString()

        load = GlobalScope.launch (Dispatchers.IO){
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(PodcastURL)
                this.prepare()
            }
        }
        binding.txtPlayerTitle.text = podName
        Glide.with(binding.episodeImageView.context).load(url).into(binding.episodeImageView)
        binding.txtTotal.text = episode.duration
        binding.imgPlay.setOnClickListener {
            if (State){
                play()
            }
            else{
                pause()
            }
        }
        binding.txtSpeed.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (load.isCompleted){ // just in case the prepare() is not executed
                    mediaPlayer?.playbackParams?.speed = 4F
                }
            }
        }
        // to enable seeking of audio from seekbar
        binding.seekBarEpisode.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2 && load.isCompleted){
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
            if (load.isCompleted){
                val newtime = mediaPlayer!!.currentPosition - 5000
                if (newtime>0){
                    mediaPlayer!!.seekTo(newtime)
                }else{
                    mediaPlayer!!.seekTo(0)
                }
            }
        }
        binding.imgNext.setOnClickListener {
            if (load.isCompleted){
                val newtime = mediaPlayer!!.currentPosition + 5000
                if (newtime<mediaPlayer!!.duration){
                    mediaPlayer!!.seekTo(newtime)
                }
            }
        }
    }

    fun play(){
        State = false
        binding.imgPlay.setImageResource(R.drawable.ic_baseline_pause_24)
        binding.progressBar.visibility = View.VISIBLE // to show loading till the prepare() is ready
        load.invokeOnCompletion {// wait for prepare() to be ready
            mediaPlayer?.start()
            runOnUiThread { // to update UI on the main thread
                binding.progressBar.visibility = View.GONE
                binding.seekBarEpisode.progress = mediaPlayer!!.currentPosition
                binding.seekBarEpisode.max = mediaPlayer!!.duration
                val handler = Handler()
                handler.postDelayed(object : Runnable { // to update the values of seekbar and curr time continually
                    override fun run() {
                        try {
                            binding.seekBarEpisode.progress = mediaPlayer!!.currentPosition
                            binding.txtCurrent.text = convert(binding.seekBarEpisode.progress)
                            handler.postDelayed(this, 1000)
                            if (!mediaPlayer!!.isPlaying){
                                binding.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_black)
                                State = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }, 0)
            }
        }

    }

    fun pause(){
        State = true
        binding.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_black)
        mediaPlayer?.pause()
    }

    fun convert(duration:Int): String { // to convert millsec to minutes and seconds
        return String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        )
    }

    override fun onBackPressed() {
        if(State){
            mediaPlayer?.release()
            mediaPlayer = null
            super.onBackPressed()
        }else{
            MaterialAlertDialogBuilder(this).setTitle("Stop the player?")
                .setMessage("Going Back will stop the podcast, do you still want to go back?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    mediaPlayer?.release()
                    mediaPlayer = null
                    super.onBackPressed()
                }
                .setNegativeButton("No"){dialoge,i->
                }
                .create()
                .show()
        }

    }
}