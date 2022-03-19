package com.example.youtubeconverter.data

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import java.io.FileNotFoundException

class ExternalStorageService {
    private var tag : String = "ExternalStorageService"
    private var fileName: String = "%YouTube Converter%"
    private lateinit var songList : List<SongItem>

    fun searchExternalStorage(contentResolver: ContentResolver): List<SongItem> {
        try {
            songList = listOf() // Reset list to prevent duplicates

            //Uri of songs
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            //What we want the cursor to return from the song
            val projection = arrayOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
            )

            //Have the cursor only find music files and files in the chosen folder
            val selection =
                MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.DATA + " LIKE ? "

            //Create the cursor to point at audio files
            val song: Cursor? =
                contentResolver.query(uri, projection, selection, arrayOf(fileName), null)

            //If the cursor is not null, check each item
            if (song != null) {
                while (song.moveToNext()) {
                    //Get the data from the cursor
                    val path = song.getString(0)
                    val title = song.getString(1)
                    val artist = song.getString(2)
                    val duration = song.getString(3)

                    //Add the song to the list if it is an mp3
                    if (path != null && path.endsWith(".mp3")) {
                        songList = songList + SongItem(title, artist, path, duration)
                    }
                }
                song.close()
            }
        } catch (error: FileNotFoundException) {
            Log.d(tag, "File not found")
        }

        return songList
    }
}