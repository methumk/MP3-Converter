package com.example.youtubeconverter.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubeconverter.api.YoutubeService
import com.example.youtubeconverter.data.VideoQueryLoadingStatus
import com.example.youtubeconverter.data.YoutubeSearchResults
import com.example.youtubeconverter.data.YoutubeVideoRepository
import kotlinx.coroutines.launch

/**
 * View Model to manage making api calls to get search result data
 * */

class SearchResultsViewModel : ViewModel() {
    private val tag: String = "SearchResultViewModel"
    //repository method
    private val repository = YoutubeVideoRepository(YoutubeService.createService())

    //Holds the parsed video search results
    private val _searchResults = MutableLiveData<YoutubeSearchResults?>(null)
    val searchResults: LiveData<YoutubeSearchResults?> = _searchResults

    //indicates if there was an error from the API in fetching search results
    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error

    //Indicates if API is loading
    private val _loading = MutableLiveData(VideoQueryLoadingStatus.SUCCESS)
    val loading: LiveData<VideoQueryLoadingStatus> = _loading

    /**
     * Uses the Youtube API to get data for a search query
     *
     * @param videoSearchVal Video Search Query
     * @Param maxResults Maximum number of results for API to return (0 to 50 inclusive)
     * @Param videoDuration (vals are any, long, medium, short) for the type of video per query
     * */
    fun getVideoSearchResults(
        videoSearchVal: String?,
        maxResults: UInt?,
        videoDuration: String?,
        apiKey: String
    ){

        //make default max number of results 10 if its null
        var numResults: UInt = maxResults ?: 10U

        //make sure maxResults is not greater than 50
        if (numResults > 50U)
            numResults = 50U

        var vidDuration: String = videoDuration ?: "any"

        viewModelScope.launch {
           _loading.value = VideoQueryLoadingStatus.LOADING
           val result = repository.loadYoutubeSearch(videoSearchVal, numResults, vidDuration, apiKey)
           _error.value = result.exceptionOrNull()
           _searchResults.value = result.getOrNull()
            _loading.value = when (result.isSuccess){
                true -> VideoQueryLoadingStatus.SUCCESS
                false -> VideoQueryLoadingStatus.ERROR
            }
        }
    }


}