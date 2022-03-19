package com.example.youtubeconverter.ui

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.example.youtubeconverter.data.SongItem

class MediaPlayerViewModel : ViewModel() {
    private var currentSong: SongItem? = null // The current song being played
    private var player : MediaPlayer = MediaPlayer() // The media player to play songs

    //Returns the current song
    fun getCurrentSong() : SongItem? {
        return currentSong
    }

    //Sets the current song to the passed in song
    fun setSong(songItem: SongItem){
        currentSong = songItem // Gets the previous song in the list
        cleanAndStart() // Cleans the old player and starts up a new one
    }

    //Returns if the player is playing or not
    fun isPlaying() : Boolean{
        return player.isPlaying
    }

    //Returns the current song time
    fun getPos() : Int {
        return player.currentPosition
    }

    //Sets the song time
    fun setSongPos(pos : Int) {
        player.seekTo(pos)
    }

    //Moves the player back by 10 seconds
    fun skipBack10(){
        if (currentSong != null) {
            val newTime: Int = player.currentPosition - 10000
            player.seekTo(newTime)
        }
    }

    //Plays the current song in the payer
    fun play() : Boolean{
        if (currentSong != null) {
            player.start() // Resume the audio
            return true
        }
        return false
    }

    //Pauses the current song in the player
    fun pause() : Boolean{
        if (currentSong != null) {
            player.pause() // Pause the audio
            return true
        }
        return false
    }

    //Moves the player forward by 10 seconds
    fun skipForward10(){
        if (currentSong != null) {
            val newTime: Int = player.currentPosition + 10000
            player.seekTo(newTime)
        }
    }

    //Adds a listener to the player
    fun setListener(listener : MediaPlayer.OnCompletionListener){
        player.setOnCompletionListener(listener) // Sets a listener for when a song ends
    }

    //Removes a listener from the player
    fun removeListener(){
        player.setOnCompletionListener(null)
    }

    //Resets the old player, creates a new player with the current song, and updates song info on bottom bar
    private fun cleanAndStart(){
        player.reset() // Resets the media player
        if (currentSong != null) {
            player.setDataSource(currentSong!!.songURL) // Sets the source to be the mp3's url
            player.prepare() // Prepares the player for playback
            player.start() // Starts playing using the player
        }
    }
}