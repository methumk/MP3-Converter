package com.example.youtubeconverter.api

import android.util.Log
import com.example.youtubeconverter.data.YoutubeQueryJsonAdapter
import com.example.youtubeconverter.data.YoutubeSearchResults
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val tag = "YoutubeService"
interface YoutubeService {
    /**
     * Sample Search Query:
     * specify type=videos to avoid searching for channels
     * @param part Set to snippet
     * @param searchQuery The query the user wants to search for, plugged into parameter "q" in youtube api
     * @param maxResults The max number of items that should be returned in result. Values range from 0 to 50 inclusive.
     * @param queryType Search type; set to video
     * @param apiKey youtube api key
     *
     * https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&q=dunkey&type=video&videoDuration=any&key=AIzaSyC-DiR4D6E4m2XEcQowJQHXNJv6wa3hZ9c
     * */
    @GET("search")
    suspend fun searchYoutubeVideos(
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: UInt?,
        @Query("q") q: String?,
        @Query("videoDuration") videoDuration: String,
        @Query("type") type: String = "video",
        @Query("key") key: String
    ) : YoutubeSearchResults


    companion object {
        private const val BASE_URL = "https://youtube.googleapis.com/youtube/v3/"

        fun createService() : YoutubeService{
            val moshi = Moshi.Builder()
                .add(YoutubeQueryJsonAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(YoutubeService::class.java)
        }
    }
}