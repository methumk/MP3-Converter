package com.example.youtubeconverter.data

import com.squareup.moshi.FromJson
import java.io.Serializable

data class VideoQueryItem(
    /**
        videoUrl: complete url link to video
        publishedTime: time when video was published
        videoAuthor: creator of the video
     */
    val videoUrl: String,
    val videoTitle: String,
    val videoAuthor: String,
    val videoDescription: String,
    val publishedTime: String,

    /**
    *   Thumbnail urls contains the full url to the image for default, medium, high images
    * */
    val defaultThumbUrl: String,
    val defaultThumbWidth: Int,
    val defaultThumbHeight: Int,

    val mediumThumbUrl: String,
    val mediumThumbWidth: Int,
    val mediumThumbHeight: Int,

    val highThumbUrl: String,
    val highThumbWidth: Int,
    val highThumbHeight: Int

) : Serializable


/**
 * These classes represent the Item elements part of the Json
 * YoutubeVideoItem is an element of the Item array
 * https://developers.google.com/youtube/v3/docs/search#resource
 * */
//VideoItems represent an individual element in the items array
data class YoutubeVideoItem(
    //id: contains value corresponding to the videoID which is part of the youtube url
    val id: ID,
    //snippet: contains values corresponding to video title, description, and thumbnails
    val snippet: VideoInfo,
)

data class ID(
    //part of the youtube query to get to the video, e.g. youtube.com/watch?v=videoID
    val videoId: String
)

data class VideoInfo(
    val publishedAt: String,
    //title: title of video
    val title: String,
    //description: video description
    val description: String,
    //thumbnails: different size for the thumbnail images
    val thumbnails: ThumbSizes,
    //channelTitle: author of the video
    val channelTitle: String
)

data class ThumbSizes(
    //corresponds to thumbnail image size and quality
    val default: ThumbItems,
    val medium: ThumbItems,
    val high: ThumbItems
)

data class ThumbItems(
    //url: url to thumbnail image
    val url: String,
    //width: width of image
    val width: Int,
    //height: height of image
    val height: Int
)

/**
 * Custom Moshi type adapter
 * Parses Item element from YoutubeVideoItem to VideoQueryItems
 * */
class YoutubeQueryJsonAdapter{
    @FromJson
    fun queryItemsFromJson(video: YoutubeVideoItem) = VideoQueryItem(
        publishedTime = video.snippet.publishedAt,
        videoUrl = "https://www.youtube.com/watch?v=${video.id.videoId}",
        videoAuthor = video.snippet.channelTitle,
        videoTitle = video.snippet.title,
        videoDescription = video.snippet.description,

        defaultThumbUrl = video.snippet.thumbnails.default.url,
        defaultThumbWidth = video.snippet.thumbnails.default.width,
        defaultThumbHeight = video.snippet.thumbnails.default.height,

        mediumThumbUrl = video.snippet.thumbnails.medium.url,
        mediumThumbWidth = video.snippet.thumbnails.medium.width,
        mediumThumbHeight = video.snippet.thumbnails.medium.height,

        highThumbUrl = video.snippet.thumbnails.high.url,
        highThumbWidth = video.snippet.thumbnails.high.width,
        highThumbHeight = video.snippet.thumbnails.high.height
    )
}