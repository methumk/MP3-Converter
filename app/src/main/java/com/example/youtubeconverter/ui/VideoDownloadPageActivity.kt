package com.example.youtubeconverter.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.R.id
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.example.youtubeconverter.R
import com.example.youtubeconverter.data.VideoQueryItem
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths


const val EXTRA_VIDEO_ITEM = "com.example.android.youtubeconverter.VIDEO_QUERY_ITEM"
class VideoDownloadPageActivity: AppCompatActivity() {
    private val DownloadApiUrl = "http://youtube-downloader-converter.herokuapp.com"

    //private val storageViewModel : ExternalStorageViewModel by viewModels()

    private var videoItem: VideoQueryItem? = null

    private lateinit var convertButton: Button
    private lateinit var customTitle: EditText
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var youtubeButton: Button

    var downloadid: Long = 0
    var titleValue = ""
    var remainingAttempts: Int = 5
    var receiverRegistered: Boolean = false

    //broadcast receiver for completed download, called in download function
    //https://camposha.info/android-examples/android-downloadmanager/#gsc.tab=0
    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {//check if download was completed
                Log.d("Download", "Complete")

                val title = titleValue
                val baseLocation = "/storage/emulated/0/"
                val location = baseLocation + Environment.DIRECTORY_MUSIC + "/Youtube Converter/"

                //This is really important
                //Download is seen as complete regardless of actual success
                //this sets a file to be what the downloaded file should be if it was successful
                val file: File = File(location+"$title.mp3")

                if(file.exists()){//If the file exists that all is good, displays a success snackbar
                    Log.d("Exists","True!")
                    Snackbar.make(
                        window.decorView.rootView,
                        "Download Complete",
                        Snackbar.LENGTH_LONG
                    ).show()
                    convertButton.visibility = View.VISIBLE
                    customTitle.visibility = View.VISIBLE
                    youtubeButton.visibility = View.VISIBLE
                    loadingIndicator.visibility = View.INVISIBLE
                }else{//If the file doesn't exist, then either try again or give up
                    Log.d("Exists","False! :(")
                    if(remainingAttempts > 0){//tries 5 times to download the file, gives up after that
                        remainingAttempts -= 1
                        download(videoItem!!.videoUrl,titleValue)
                    }else{//after the attempts, display and error and stop the download
                        Snackbar.make(
                            window.decorView.rootView,
                            "Error: Download Failed to Complete",
                            Snackbar.LENGTH_LONG
                        ).show()
                        convertButton.visibility = View.VISIBLE
                        customTitle.visibility = View.VISIBLE
                        youtubeButton.visibility = View.VISIBLE
                        loadingIndicator.visibility = View.INVISIBLE
                    }
                }
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(receiverRegistered) {//unregister the receiver if it is registered
            Log.d("Unregistering","broadcastReceiver")
            unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_page)

        convertButton = findViewById(R.id.btn_convert_vid)
        customTitle = findViewById(R.id.custom_song_edit_text)
        loadingIndicator = findViewById(R.id.download_loading_indicator)
        youtubeButton = findViewById(R.id.btn_vid_listen)



        if (intent != null && intent.hasExtra(EXTRA_VIDEO_ITEM)){//gets the video item
            videoItem = intent.getSerializableExtra(EXTRA_VIDEO_ITEM) as VideoQueryItem
            Glide.with(this)
                .load(videoItem!!.highThumbUrl)
                .into(findViewById(R.id.iv_high_convert_thumbnail))



            convertButton.setOnClickListener{//listener for the convert song button

                //Hides the keyboard
                val view: View = window.decorView.rootView
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)


                Log.d("Video Url: ",videoItem!!.videoUrl)
                var videoTitle = videoItem!!.videoTitle//gets the video title

                //checks if the edit text is empty, if not, it sets the title to the contents
                if(!customTitle.text.toString().isNullOrEmpty()){
                    videoTitle = customTitle.text.toString()
                }

                titleValue = videoTitle

                //check download location to see if there is already a file with that name, give error if there is
                val baseLocation = "/storage/emulated/0/"
                val location = baseLocation + Environment.DIRECTORY_MUSIC + "/Youtube Converter/"
                //Log.d("Checking Location",location)

                val file: File = File(location+"$titleValue.mp3")

                if(file.exists()){
                    Snackbar.make(
                        window.decorView.rootView,
                        "Error: Song Name Already Exists in Library",
                        Snackbar.LENGTH_LONG
                    ).show()
                }else {//If the file isn't there, display the loading circle and download the song

                    convertButton.visibility = View.INVISIBLE
                    customTitle.visibility = View.INVISIBLE
                    youtubeButton.visibility = View.INVISIBLE
                    loadingIndicator.visibility = View.VISIBLE

                    download(videoItem!!.videoUrl, titleValue)

                    Snackbar.make(
                        window.decorView.rootView,
                        "Downloading Song: " + titleValue,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }



            // Set listener to view song in youtube
            youtubeButton.setOnClickListener {

                // Intent for Youtube App
                var appString = videoItem!!.videoUrl
                appString = appString.replace("https://www.youtube.com/watch?v=", "vnd.youtube:")
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(appString))

                // Intent for web browser
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(videoItem!!.videoUrl)
                )

                try {
                    startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    startActivity(webIntent)
                }
            }
        }




    }

    private fun download(url: String, name: String){
        val fullUrl = "$DownloadApiUrl/download?type=mp3&url=$url"//Api: https://github.com/joaohudson/youtube-downloader-converter-mobile?ref=androidexample365.com

        //Download request for the mp3 supplied by the api
        //https://camposha.info/android-examples/android-downloadmanager/#gsc.tab=0
        val downloadRequest = DownloadManager.Request(Uri.parse(fullUrl))
        downloadRequest.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
        downloadRequest.setTitle("$name.mp3")//sets the name of the download
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)//notify on completion
        downloadRequest.setDestinationInExternalPublicDir(//set directory to the one Michael created
            Environment.DIRECTORY_MUSIC,
            "Youtube Converter/$name.mp3"
        )
        downloadRequest.setMimeType("audio/mpeg") //https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types

        val downloadManager = applicationContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadid = downloadManager.enqueue(downloadRequest)//make the download request
        //Log.d("Made it here","download ran")
        //registerReceiver(new, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))//set up listener for completed download
        receiverRegistered = true
    }
}



