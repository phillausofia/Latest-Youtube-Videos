package com.example.latest_youtube_videos.network

import com.example.latest_youtube_videos.BuildConfig
import com.example.latest_youtube_videos.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {

    @GET(Utils.SEARCH_END_POINT)
    fun getGmbnVideos(
        @Query("maxResults") maxResults: Int,
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("type") type: String = "video",
        @Query("pageToken") nextPageToken: String? = null,
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY,
        @Query("order") order: String = "Date"
    ): Deferred<SearchEndPointResultModel>

    @GET(Utils.VIDEOS_END_POINT)
    fun getVideoDetails(
        @Query("id") id: String,
        @Query("part") part: String = "snippet,ContentDetails",
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY
    ): Deferred<VideosEndPointResultModel>

    @GET(Utils.COMMENT_THREADS_END_POINT)
    fun getTopLevelComments(
        @Query("videoId") videoId: String,
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") nextPageToken: String?,
        @Query("textFormat") textFormat: String = "plainText",
        @Query("part") part: String = "snippet",
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY,
        @Query("order") order: String = "time"
    ): Deferred<CommentThreadsEndPointResultModel>
}