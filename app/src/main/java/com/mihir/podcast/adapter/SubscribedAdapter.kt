package com.mihir.podcast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mihir.podcast.model.SubscriptionClass
import com.mihir.podcast.ui.databinding.ItemSubscriptionBinding

class SubscribedAdapter(val list:ArrayList<SubscriptionClass>):RecyclerView.Adapter<SubscribedAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemSubscriptionBinding):RecyclerView.ViewHolder(binding.root){
        val Image:ImageView = binding.img
        val title:TextView = binding.txtTitle
        val date:TextView = binding.txtDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context),
            parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].title
        holder.date.text = list[position].date.date.toString()
        Glide.with(holder.itemView.context).load(list[position].imgUrl).into(holder.Image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}