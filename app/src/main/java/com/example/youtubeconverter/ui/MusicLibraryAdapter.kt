package com.example.youtubeconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubeconverter.R
import com.example.youtubeconverter.data.SongItem

/* Recycler view for music library */
/* Needs title, artist, duration, url */
class MusicLibraryAdapter (private val onClick: (SongItem) -> Unit) : RecyclerView.Adapter<MusicLibraryAdapter.ViewHolder>() {
    var songList: List<SongItem> = listOf()

    fun addSong(newSong: SongItem){
        val newList = songList + newSong
        songList = newList
        notifyDataSetChanged()
    }

    fun updateSongList(newSongList: List<SongItem>?){
        if (newSongList != null) {
            songList = newSongList
        }
        notifyDataSetChanged()
    }

    fun songPosition(song: SongItem): Int{
        return this.songList.indexOf(song)
    }

    fun getSong(position: Int): SongItem{
        var pos = position
        if (pos < 0){
            pos += this.songList.size
        }
        pos %= this.songList.size

        return songList.get(pos)
    }

    fun getSongByUrl(url : String) : SongItem?{
        return this.songList.firstOrNull { it.songURL == url }
    }

    override fun getItemCount() = this.songList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_library_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: MusicLibraryAdapter.ViewHolder, position: Int) {
        holder.bind(this.songList[position])
    }

    class ViewHolder(itemView: View, val onClick: (SongItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_title)
        private val author: TextView = itemView.findViewById(R.id.tv_artist)
        private val duration: TextView = itemView.findViewById(R.id.tv_duration)
        private lateinit var url: String
        private var currentSongItem: SongItem? = null

        init{
            itemView.setOnClickListener{
                currentSongItem?.let(onClick)
            }
        }

        fun bind(song: SongItem){
            currentSongItem = song
            title.text = song.title
            author.text = song.artist
            url = song.songURL

            //Change author to None if there is none
            if (author.text == "<unknown>"){
                "None".also { author.text = it }
            }

            //Set duration
            duration.text = milliToTime(song.duration.toInt())
        }

        //Converts milliseconds to hours:minutes:seconds
        private fun milliToTime(milli : Int) : String{
            val hours = milli / (60 * 60 * 1000)
            val minutes = (milli % (60 * 60 * 1000)) / (60 * 1000)
            val seconds = (milli % (60 * 60 * 1000) % (60 * 1000)) / 1000
            var convertedTime = ""

            //Add hours
            if (hours > 0){
                convertedTime += "${hours}:"
            }

            //Add minutes
            if (hours > 0 || minutes > 0){
                convertedTime += "${minutes}:"
            }

            //Add seconds
            convertedTime += "$seconds"

            return convertedTime
        }
    }
}