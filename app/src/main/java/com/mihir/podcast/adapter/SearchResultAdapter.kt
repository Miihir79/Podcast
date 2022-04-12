package com.mihir.podcast.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mihir.podcast.helper.DateUtils
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.PodcastDetails
import com.mihir.podcast.ui.R
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.ui.MainActivity
import com.mihir.podcast.ui.databinding.ItemSubscriptionBinding
import kotlin.collections.ArrayList

class SearchResultAdapter(
    private val list: ArrayList<SearchClass>,
    private val activity: Activity,
    private val viewModel: FavViewModel
):RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemSubscriptionBinding):RecyclerView.ViewHolder(binding.root){
        val image = binding.img
        val title = binding.txtTitle
        val updated = binding.txtDate
        val view = binding.root
        val like = binding.imgLike
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context),
            parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.image.context).load(list[position].imageUrl).placeholder(R.drawable.loading).into(holder.image)
        holder.title.text = list[position].name
        holder.updated.text = list[position].lastUpdated.let {
            DateUtils.jsonDateToShortDate(jsonDate = it)
        }
        holder.view.setOnClickListener {
            val intent = Intent(holder.view.context,PodcastDetails::class.java)
            intent.putExtra("Search",list[position])
            val pair1=  Pair.create<View, String>(holder.image,"img_small")
            val pair2 =  Pair.create<View, String>(holder.title,"title")
            val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2)
            holder.view.context.startActivity(intent,transition.toBundle())
        }
        holder.like.setOnClickListener {
            viewModel.addFav(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}