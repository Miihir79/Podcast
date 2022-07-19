package com.mihir.podcast.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mihir.podcast.helper.DateUtils
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.PodcastDetails
import com.mihir.podcast.ui.R
import androidx.core.util.Pair
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.ui.databinding.ItemSubscriptionBinding
import kotlin.collections.ArrayList

class SearchResultAdapter(
    private val activity: Activity,
    private val viewModel: FavViewModel,
    private val isFavCall:Boolean
):RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
    private var list= ArrayList<SearchClass>()

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
        if(list[position].isLiked){
            holder.like.background = AppCompatResources.getDrawable(activity,R.drawable.iosheart)
        }else{
            holder.like.background = AppCompatResources.getDrawable(activity,R.drawable.ic_icon_ionic_md_heart_empty)
        }
        holder.updated.text = list[position].lastUpdated.let {
            DateUtils.jsonDateToShortDate(jsonDate = it)
        }
        holder.view.setOnClickListener {
            onclick(holder,position)
        }
        holder.like.setOnClickListener {
            val updated = list[position]
            if(isFavCall){
                updated.isLiked = false
                viewModel.deleteFav(list[position])
                removeFromList(position)
            }else{
                updated.isLiked = true
                viewModel.addFav(list[position])
                Toast.makeText(activity,"Added to liked",Toast.LENGTH_SHORT).show()
            }
            updateList(position,updated)
        }
    }
    private fun onclick(holder: ViewHolder, position: Int) {
        val intent = Intent(activity,PodcastDetails::class.java)
        intent.putExtra("Search",list[position])
        val pair1=  Pair.create<View, String>(holder.image,"img_small")
        val pair2 =  Pair.create<View, String>(holder.title,"title")
        val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2)
        activity.startActivity(intent,transition.toBundle())
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(newList:ArrayList<SearchClass>){
        list = newList
        notifyDataSetChanged()
    }

    fun updateList(position:Int, new :SearchClass){
        list[position] = new
        notifyItemChanged(position,list)
    }

    fun removeFromList(position: Int){
        notifyItemRemoved(position)
    }
}