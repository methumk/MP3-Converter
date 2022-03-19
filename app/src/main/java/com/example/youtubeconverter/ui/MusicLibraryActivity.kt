package com.example.youtubeconverter.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubeconverter.R
import com.example.youtubeconverter.data.SongItem
import com.example.youtubeconverter.data.StorageLoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

class MusicLibraryActivity : AppCompatActivity() {
    private val tag = "MusicLibraryActivity"
    private lateinit var songAdapter: MusicLibraryAdapter // Song adapter

    private val storageViewModel : ExternalStorageViewModel by viewModels()  // Holds the songs from external storage
    private val playerViewModel : MediaPlayerViewModel by viewModels() // Holds the player

    //Buttons
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var previousSongButton: ImageView
    private lateinit var nextSongButton: ImageView
    private lateinit var skipForwardButton: ImageView
    private lateinit var skipBackwardButton: ImageView

    // Different music library elements
    private lateinit var songListRV: RecyclerView //The list of songs
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    //Listener for song ending
    private val songEndListener : MediaPlayer.OnCompletionListener = MediaPlayer.OnCompletionListener {
        playerViewModel.setSong(getNextSong()) //Gets the next song
        updateBarSong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_library)
        supportActionBar?.title = "Music Library"

        //Set up page elements
        songListRV = findViewById(R.id.rv_music_library)
        searchErrorTV = findViewById(R.id.tv_search_error)
        loadingIndicator = findViewById(R.id.loading_indicator)

        //Setup buttons
        playButton = findViewById(R.id.music_library_play)
        pauseButton = findViewById(R.id.music_library_pause)
        previousSongButton = findViewById(R.id.music_library_previous)
        nextSongButton = findViewById(R.id.music_library_next)
        skipForwardButton = findViewById(R.id.music_library_skip_forward)
        skipBackwardButton = findViewById(R.id.music_library_skip_backward)

        //Set up music library recycler view
        songAdapter = MusicLibraryAdapter(::onSongClick)
        songListRV = findViewById(R.id.rv_music_library)
        songListRV.setHasFixedSize(true)
        songListRV.layoutManager = LinearLayoutManager(this)
        songListRV.adapter = songAdapter

        //Fetch songs on startup using a view model approach
        storageViewModel.loadSearchResults(contentResolver)

        // Observe the songs stored in the view model and update the song list
        storageViewModel.searchResults.observe(this){ searchResults ->
            songAdapter.updateSongList(searchResults) // Update the song list
            songListRV.scrollToPosition(0) // Go to top of song list
        }

        //Observe the status of fetching songs from storage
        storageViewModel.loadingStatus.observe(this) { loadingStatus ->
            when (loadingStatus) {
                StorageLoadingStatus.LOADING -> {
                    loadingIndicator.visibility = View.VISIBLE
                    songListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                StorageLoadingStatus.FAILURE -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    songListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    songListRV.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
            }
        }

        //Previous song button, will change the song to the previous one in the list
        previousSongButton.setOnClickListener {
            if (playerViewModel.getCurrentSong() != null) {
                playerViewModel.setSong(getPreviousSong())
                updateBarSong()
            }
        }

        //Skip back button, will add 10 seconds to the current position
        skipBackwardButton.setOnClickListener {
            playerViewModel.skipBack10()
        }

        //What happens when the play button is clicked
        playButton.setOnClickListener{
            if (playButton.visibility == View.VISIBLE && playerViewModel.play()){
                showPauseButton()
            }
        }

        //What happens when the pause button is clicked
        pauseButton.setOnClickListener{
            if (pauseButton.visibility == View.VISIBLE && playerViewModel.pause()){
                showPlayButton()
            }
        }

        //Skip forward button, will remove 10 seconds from the current position of the song
        skipForwardButton.setOnClickListener {
            playerViewModel.skipForward10()
        }

        //The next song button, will change the song to the next one in the list
        nextSongButton.setOnClickListener {
            if (playerViewModel.getCurrentSong() != null) {
                playerViewModel.setSong(getNextSong())
                updateBarSong()
            }
        }
    }

    // Play the song that has been clicked
    private fun onSongClick(song: SongItem) {
        playerViewModel.setSong(song)
        updateBarSong()
        playerViewModel.setListener(songEndListener)
    }

    //Shows the play button and hides the pause button
    private fun showPlayButton(){
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.INVISIBLE
    }

    //Shows the pause button and hides the play button
    private fun showPauseButton(){
        playButton.visibility = View.INVISIBLE
        pauseButton.visibility = View.VISIBLE
    }

    //Sets the next song to current
    private fun getNextSong() = songAdapter.getSong(songAdapter.songPosition(playerViewModel.getCurrentSong()!!) + 1)

    //Sets the previous song to current
    private fun getPreviousSong() = songAdapter.getSong(songAdapter.songPosition(playerViewModel.getCurrentSong()!!) - 1)

    //Updates the song info on the bottom bar
    private fun updateBarSong(){
        val currentSong = playerViewModel.getCurrentSong()

        if (currentSong != null) {
            findViewById<TextView>(R.id.music_library_current_title).text = currentSong.title
            findViewById<TextView>(R.id.music_library_song_progress).text = milliToTime(currentSong.duration.toInt())
            showPauseButton()
        }
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

    //Updates the song list
    private fun refreshSongList(){
        storageViewModel.loadSearchResults(contentResolver)
    }

    //Creates a custom options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.music_library_menu, menu)
        return true
    }

    //Actions when an option menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                refreshSongList()
                true
            }
            else -> {
                playerViewModel.pause()
                super.onOptionsItemSelected(item)
            }
        }
    }

    //Stop player from playing when going to main menu
    override fun onBackPressed() {
        super.onBackPressed()

        playerViewModel.pause()
    }

    //Trying to refill the bottom bar for when the user goes to the main menu
    override fun onResume() {
        super.onResume()

        //Restores the action bar to normal when resuming (device rotation)
        if (playerViewModel.getCurrentSong() != null){
            updateBarSong()
            if (playerViewModel.isPlaying()){
                showPauseButton()
            }
        }
    }
}