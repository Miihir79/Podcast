package com.mihir.podcast.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mihir.podcast.adapter.SubscribedAdapter
import com.mihir.podcast.model.SubscriptionClass
import com.mihir.podcast.remote.ItunesGet
import com.mihir.podcast.ui.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.gray)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val list = arrayListOf<SubscriptionClass>()
        list.add(SubscriptionClass(0,"https://picsum.photos/300/300?random=1","First", date = Date()))
        binding.recyclerview.adapter = SubscribedAdapter(list)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.imgSearch.setOnClickListener {
            val intent = Intent(this,SearchResult::class.java)
            startActivity(intent)
        }


    }

}