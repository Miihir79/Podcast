package com.mihir.podcast.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mihir.podcast.helper.DateUtils
import com.mihir.podcast.model.FavViewModel
import com.mihir.podcast.model.SearchClass
import com.mihir.podcast.ui.R
import com.mihir.podcast.ui.databinding.ItemSubscriptionBinding

class SearchResultAdapter(
    private val activity: Activity,
    private val viewModel: FavViewModel,
    private val isFavCall: Boolean,
    private val onItemClicked: ((searchItem: SearchClass, pair1: Pair<View, String>, pair2: Pair<View, String>) -> Unit)
) : ListAdapter<SearchClass, SearchResultAdapter.ViewHolder>(ItemCallback) {

    object ItemCallback : DiffUtil.ItemCallback<SearchClass>() {
        override fun areItemsTheSame(oldItem: SearchClass, newItem: SearchClass): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SearchClass, newItem: SearchClass): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    inner class ViewHolder(binding: ItemSubscriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.img
        val title = binding.txtTitle
        val updated = binding.txtDate
        val view = binding.root
        val like = binding.imgLike
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.image.context).load(currentList[position].imageUrl).placeholder(R.drawable.loading).into(holder.image)
        holder.title.text = currentList[position].name
        if (currentList[position].isLiked) {
            holder.like.background = AppCompatResources.getDrawable(activity, R.drawable.iosheart)
        } else {
            holder.like.background = AppCompatResources.getDrawable(activity, R.drawable.ic_icon_ionic_md_heart_empty)
        }
        holder.updated.text = currentList[position].lastUpdated.let {
            DateUtils.jsonDateToShortDate(jsonDate = it)
        }
        holder.view.setOnClickListener {
            val pair1 = Pair.create<View, String>(holder.image, "img_small")
            val pair2 = Pair.create<View, String>(holder.title, "title")
            onItemClicked(currentList[position], pair1, pair2)
        }
        holder.like.setOnClickListener {
            if (isFavCall) {    // is called form fav page (so delete if clicked on the fav button)
                currentList[position].isLiked = false
                viewModel.deleteFav(currentList[position])
            } else {    // called from search page
                if (currentList[position].isLiked) {
                    currentList[position].isLiked = false
                    viewModel.deleteFav(currentList[position])
                } else {
                    currentList[position].isLiked = true
                    viewModel.addFav(currentList[position])
                    Toast.makeText(activity, "Added to liked", Toast.LENGTH_SHORT).show()
                }
                notifyItemChanged(position)
            }
        }
    }

    var list: List<SearchClass> = emptyList()
        set(value) {
            field = value
            submitList(field)
        }

}