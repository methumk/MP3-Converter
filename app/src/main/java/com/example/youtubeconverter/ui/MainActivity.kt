package com.example.youtubeconverter.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.youtubeconverter.R
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.youtubeconverter.BuildConfig

// @TODO My build config is not working, the api key is in gradle.properties
//const val YOUTUBE_APPID = BuildConfig.YOUTUBE_API_KEY

//AIzaSyCkQa2gnFNkvvSY-G2l_jOYl2SdInRjz24
//AIzaSyC-DiR4D6E4m2XEcQowJQHXNJv6wa3hZ9c
//AIzaSyBh9_g7_Wcr5xL3F4rXsdMcC2vJpHWadbo
//AIzaSyBk5WyyuaZt6ynKIbQPiTIvDdwyZU3D-DU
const val YOUTUBE_APPID = "AIzaSyBk5WyyuaZt6ynKIbQPiTIvDdwyZU3D-DU"

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting listeners for clicking on buttons for other pages
        val musicLibBtn: Button = findViewById(R.id.to_music_library)
        musicLibBtn.setOnClickListener {
            val intent = Intent(this, MusicLibraryActivity::class.java).apply {

            }
            startActivity(intent)
        }

        val dwnldMusicBtn: Button = findViewById(R.id.to_download_music)
        dwnldMusicBtn.setOnClickListener {
            val intent = Intent(this, MusicSearchActivity::class.java)

            startActivity(intent)
        }

        /*
        val settingBtn: Button = findViewById(R.id.to_settings)
        settingBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

         */
    }
}