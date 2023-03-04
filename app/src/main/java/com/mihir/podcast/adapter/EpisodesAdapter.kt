package com.mihir.podcast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mihir.podcast.helper.DateUtils
import com.mihir.podcast.helper.HtmlUtils
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.ui.databinding.ItemEpisodeBinding
import java.util.*

class EpisodesAdapter(
    var list: RssFeedResponse,
    val onItemClick: ((episode: RssFeedResponse.EpisodeResponse, title: TextView, desc: TextView) -> Unit)
) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.txtEpTitle
        val description = binding.txtEpDetail
        val date = binding.txtEpDate
        val extra = binding.txtEpExtra
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list.episodes?.get(position)?.title
        holder.description.text = HtmlUtils.htmlToSpannable(list.episodes?.get(position)?.description ?: "")
        holder.date.text = list.episodes?.get(position)?.pubDate.let {
            DateUtils.dateToShortDate(Date(it))
        }
        holder.extra.text = list.episodes?.get(position)?.duration
        holder.itemView.setOnClickListener {
            list.episodes?.get(position)?.let { it1 -> onItemClick(it1, holder.title, holder.description) }
        }
    }

    override fun getItemCount() = list.episodes?.size ?: 0

}