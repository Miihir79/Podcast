package com.mihir.podcast.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mihir.podcast.helper.DateUtils
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.PodcastDetails
import com.mihir.podcast.ui.R
import com.mihir.podcast.ui.databinding.ItemSubscriptionBinding
import java.util.*
import kotlin.collections.ArrayList

class SearchResultAdapter(val list : ArrayList<SearchClass>):RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemSubscriptionBinding):RecyclerView.ViewHolder(binding.root){
        val Image = binding.img
        val title = binding.txtTitle
        val updated = binding.txtDate
        val view = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context),
            parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.Image.context).load(list[position].imageUrl).placeholder(R.drawable.loading).into(holder.Image)
        holder.title.text = list[position].name
        holder.updated.text = list[position].lastUpdated.let {
            DateUtils.jsonDateToShortDate(jsonDate = it)
        }
        holder.view.setOnClickListener {
            val intent = Intent(holder.view.context,PodcastDetails::class.java)
            intent.putExtra("Title",list[position].name)
            intent.putExtra("Image",list[position].imageUrl)
            intent.putExtra("Feed",list[position].feedUrl)
            holder.view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}