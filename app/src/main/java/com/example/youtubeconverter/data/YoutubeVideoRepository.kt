package com.example.youtubeconverter.data

import android.util.Log
import com.example.youtubeconverter.api.YoutubeService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * ioDispatcher corresponds to pool of optimized threads for network I/O
 * Repository class has a method that obtains the results from a youtube video search
 * */
class YoutubeVideoRepository(
    private val service: YoutubeService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val tag: String = "YoutubeVidRespository"

    suspend fun loadYoutubeSearch(
        videoSearchVal: String?,
        maxResults: UInt,
        videoDuration:String,
        apiKey: String
    ): Result<YoutubeSearchResults> = withContext(ioDispatcher) {
        try {
            val videoResults = service.searchYoutubeVideos(
                maxResults = maxResults,
                q = videoSearchVal,
                videoDuration = videoDuration,
                key = apiKey
            )
            Result.success(videoResults)
        } catch (e: Exception) {
            Log.d(tag, e.toString())
            Result.failure(e)
        }
    }
}