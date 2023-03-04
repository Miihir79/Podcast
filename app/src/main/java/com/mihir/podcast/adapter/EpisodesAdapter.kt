package com.mihir.podcast.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.mihir.podcast.helper.DateUtils
import com.mihir.podcast.helper.HtmlUtils
import com.mihir.podcast.remote.RssFeedResponse
import com.mihir.podcast.ui.Player
import com.mihir.podcast.ui.PodcastDetails
import com.mihir.podcast.ui.databinding.ItemEpisodeBinding
import java.util.*

class EpisodesAdapter(
    var list: RssFeedResponse,
    var Url: String,
    var imgVPodcastImg: ImageView,
    var podcastDetails: PodcastDetails,
    var txtPodcastTitle: TextView
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
            val intent = Intent(holder.itemView.context, Player::class.java)
            val episode = list.episodes?.get(position)
            intent.putExtra("name", list.title)
            intent.putExtra("episode", episode)
            intent.putExtra("url", Url)
            val pair1 = Pair.create<View, String>(imgVPodcastImg, "img_small")
            val pair2 = Pair.create<View, String>(txtPodcastTitle, "title")
            val pair3 = Pair.create<View, String>(holder.title, "podName")
            val pair4 = Pair.create<View, String>(holder.description, "description")
            val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(podcastDetails, pair1, pair2, pair3, pair4)
            holder.itemView.context.startActivity(intent, transition.toBundle())
        }
    }

    override fun getItemCount() = list.episodes?.size ?: 0

}