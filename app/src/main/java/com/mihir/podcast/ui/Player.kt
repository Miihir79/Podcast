package com.mihir.podcast.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mihir.podcast.ui.databinding.ActivityPlayerBinding

class Player : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}