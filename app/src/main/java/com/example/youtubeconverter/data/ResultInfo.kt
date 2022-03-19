package com.example.youtubeconverter.data

/**
 * Contains Information about the total results for a query
 * May not be necessary
 * */
data class ResultInfo(
    //Total results for a query
    val totalResults: Int,
    //The number of results that will be displayed per page
    val resultsPerPage: Int
)