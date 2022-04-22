package com.mihir.podcast.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mihir.podcast.adapter.SearchResultAdapter
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel:FavViewModel
    private lateinit var myAdapter: SearchResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.gray)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgSearch.setOnClickListener {
            val intent = Intent(this,SearchResult::class.java)
            startActivity(intent)
        }

        viewModel = ViewModelProvider(this).get(FavViewModel::class.java)
        myAdapter = SearchResultAdapter(this,viewModel,true)
        binding.recyclerview.adapter = myAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        viewModel.readAll.observe(this) { favs ->
            myAdapter.setList(favs as ArrayList<SearchClass>)
        }

    }

}