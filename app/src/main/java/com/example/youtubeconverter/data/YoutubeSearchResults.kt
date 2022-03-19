package com.example.youtubeconverter.data

import com.squareup.moshi.Json

/**
 * Holds response from Youtube Api for video searches
 * Outline: https://developers.google.com/youtube/v3/docs/search/list
 * */
data class YoutubeSearchResults(
    //Token for next page (of query?) - NOT SURE IF NEEDED
    //val nextPageToken: String,

    //Region Code: video region, e.g. US
    //NOT SURE IF NEEDED
    //val regionCode: String,

    //Num Results Info: keep track of the total result for the query and the number of results per page
    //NOT SURE IF NEEDED
    //val pageInfo: ResultInfo,

    /**
     * Items: contains the video items that correspond to the search query
     * Notably:
     *      - Video url
     *      - Video title
     *      - Video Creator
     *      - Thumbnail urls
     *      - And more
     * */
    val items: List<VideoQueryItem>
)
