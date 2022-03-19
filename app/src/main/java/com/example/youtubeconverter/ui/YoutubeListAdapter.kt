package com.example.youtubeconverter.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubeconverter.R
import com.example.youtubeconverter.data.VideoQueryItem

class YoutubeListAdapter(private val onYoutubeVideoClick: (VideoQueryItem) -> Unit)
    : RecyclerView.Adapter<YoutubeListAdapter.VideoViewHolder>()

{
    var youtubeVideoList = listOf<VideoQueryItem>()
    var youtubeSearchEmpty: Boolean = false

    fun updateVideoList(newViewList: List<VideoQueryItem>?){
        youtubeSearchEmpty = false
        youtubeVideoList = newViewList ?: listOf()
        if (youtubeVideoList.isEmpty()){
            youtubeSearchEmpty = true
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = youtubeVideoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.youtube_video_list_item, parent, false)
        return VideoViewHolder(itemView, onYoutubeVideoClick)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(youtubeVideoList[position])
    }

    class VideoViewHolder(itemView: View, val onClick: (VideoQueryItem) -> Unit)
        : RecyclerView.ViewHolder(itemView)
    {
        //replaces youtube api string issues
        private fun fixYoutubeStringIssues(stringToFix: String): String{
            //replace apostrophes
            var retString = stringToFix.replace("&#39;","'")
            //replace ampersand
            return retString.replace("&amp;", "&")
        }



        private val videoNameTV: TextView = itemView.findViewById(R.id.tv_video_name)
        private val videoAuthorTV: TextView = itemView.findViewById(R.id.tv_video_author)
        private var currYoutubeVideoList: VideoQueryItem? = null
        private var medVidThumbnailIV: ImageView = itemView.findViewById(R.id.iv_med_video_thumbnail)

        init {
            itemView.setOnClickListener{
                currYoutubeVideoList?.let(onClick)
            }
        }

        fun bind(videoItem: VideoQueryItem){
            currYoutubeVideoList = videoItem
            videoNameTV.text = fixYoutubeStringIssues(videoItem.videoTitle)
            videoAuthorTV.text = fixYoutubeStringIssues(videoItem.videoAuthor)

            Glide.with(itemView.context).load(videoItem.mediumThumbUrl).into(medVidThumbnailIV)

        }
    }
}