package com.example.youtubeconverter.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubeconverter.R
import com.example.youtubeconverter.data.VideoQueryItem
import com.example.youtubeconverter.data.VideoQueryLoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

class MusicSearchActivity : AppCompatActivity() {
    private val tag = "MusicSearchActivity"
    private val esrPerm = Manifest.permission.READ_EXTERNAL_STORAGE
    private val eswPerm = Manifest.permission.WRITE_EXTERNAL_STORAGE

    //view model to manage fetching video search queries
    private val searchQueryViewModel: SearchResultsViewModel by viewModels()

    // Video settings: videoDuration: any, long(>20), medium(4-20), short(<4)

    //Search items
    private lateinit var searchBoxET: EditText
    private lateinit var searchResultsRV: RecyclerView
    private lateinit var searchErrorTV: TextView
    private lateinit var searchEmptyTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private val videoListAdapter = YoutubeListAdapter(::onVideoItemClick)


    //search vals - maxResults from 0-50 inclusive
    var videoDuration: String? = "any"
    val maxResults: UInt = 20U

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_search)

        //Request permissions
        if(ActivityCompat.checkSelfPermission(this, esrPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(esrPerm), 111)
        }
        if(ActivityCompat.checkSelfPermission(this, eswPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(eswPerm), 112)
        }

        searchBoxET = findViewById(R.id.et_search_box)
        searchResultsRV = findViewById(R.id.rv_search_results)
        searchErrorTV = findViewById(R.id.tv_search_error)
        searchEmptyTV = findViewById(R.id.tv_empty_search)
        loadingIndicator = findViewById(R.id.loading_indicator)
        searchResultsRV.layoutManager = LinearLayoutManager(this)
        searchResultsRV.setHasFixedSize(true)
        searchResultsRV.adapter = videoListAdapter


        //observe if api query got results
        searchQueryViewModel.searchResults.observe(this){ searchResults ->
            if (searchResults != null){
                Log.d(tag, "Fetched data " + searchResults.toString())

            }else{
                Log.d(tag, "Didn't fetch data")
            }

            videoListAdapter.updateVideoList(searchResults?.items)
            searchEmptyTV.visibility = View.INVISIBLE
            if (videoListAdapter.youtubeSearchEmpty && searchBoxET.text.toString() != ""){
                searchEmptyTV.visibility = View.VISIBLE
            }
        }

        //Observe api query errors
        searchQueryViewModel.error.observe(this){ error ->
            if (error != null){
                Log.d(tag, "error fetching")
            }
        }

        //Observe if api query is loading
        searchQueryViewModel.loading.observe(this){ uiState ->
            when (uiState){
                VideoQueryLoadingStatus.LOADING -> {
                    searchEmptyTV.visibility = View.INVISIBLE
                    loadingIndicator.visibility = View.VISIBLE
                    searchResultsRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                VideoQueryLoadingStatus.ERROR -> {
                    searchEmptyTV.visibility = View.INVISIBLE
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultsRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else ->{
                    loadingIndicator.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                    searchResultsRV.visibility = View.VISIBLE
                }
            }

        }

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val maxResults: UInt = 20U
        //val maxResults: UInt = 2U
        //val videoDuration: String? = sharedPrefs.getString(getString(R.string.pref_length_key), "any")
        videoDuration = sharedPrefs.getString(getString(R.string.pref_length_key), "any")
        Log.d(tag, "vid duration pref value: "+videoDuration)

        val searchBtn: Button = findViewById(R.id.search_button)
        searchBtn.setOnClickListener{

            //Hides the keyboard
            val view: View = currentFocus!!
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            val searchQuery = searchBoxET.text.toString()
            searchEmptyTV.visibility = View.INVISIBLE
            if (!TextUtils.isEmpty(searchQuery)) {
                Log.d(tag, "SEARCHING vid duration pref value: "+videoDuration)
                searchQueryViewModel.getVideoSearchResults(searchQuery, maxResults, videoDuration, YOUTUBE_APPID)
                searchResultsRV.scrollToPosition(0)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        videoDuration = sharedPrefs.getString(getString(R.string.pref_length_key), "any")
        Log.d(tag, "Resuming - vid dur: " + videoDuration)

        searchQueryViewModel.searchResults.observe(this){ searchResults ->
            videoListAdapter.updateVideoList(searchResults?.items)
            searchEmptyTV.visibility = View.INVISIBLE
            if (videoListAdapter.youtubeSearchEmpty && searchBoxET.text.toString() != ""){
                searchEmptyTV.visibility = View.VISIBLE
            }
        }

        //Observe api query errors
        searchQueryViewModel.error.observe(this){ error ->
            if (error != null){
                Log.d(tag, "error fetching")
            }
        }

        //Observe if api query is loading
        searchQueryViewModel.loading.observe(this){ uiState ->
            when (uiState){
                VideoQueryLoadingStatus.LOADING -> {
                    searchEmptyTV.visibility = View.INVISIBLE
                    loadingIndicator.visibility = View.VISIBLE
                    searchResultsRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                VideoQueryLoadingStatus.ERROR -> {
                    searchEmptyTV.visibility = View.INVISIBLE
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultsRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else ->{
                    loadingIndicator.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                    searchResultsRV.visibility = View.VISIBLE
                }
            }

        }

        val searchQuery = searchBoxET.text.toString()
        searchEmptyTV.visibility = View.INVISIBLE
        if (!TextUtils.isEmpty(searchQuery)) {
            Log.d(tag, "SEARCHING vid duration pref value: "+videoDuration)
            searchQueryViewModel.getVideoSearchResults(searchQuery, maxResults, videoDuration, YOUTUBE_APPID)
            searchResultsRV.scrollToPosition(0)
        }
    }



    private fun onVideoItemClick(video: VideoQueryItem){
        Log.d(tag, "Clicked on video: "+video.videoTitle)

        //launch video download page
        val intent = Intent(this, VideoDownloadPageActivity::class.java).apply {
            putExtra(EXTRA_VIDEO_ITEM, video)
        }
        startActivity(intent)

    }

    //Creates a custom options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.music_search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_settings -> {
                val intent = Intent(this, SearchSettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}