package com.example.latest_youtube_videos.network

import com.example.latest_youtube_videos.utils.Utils
import com.example.latest_youtube_videos.utils.toDate
import com.example.latest_youtube_videos.utils.toString

data class SearchEndPointResultModel(
    val nextPageToken: String?,
    val items: List<Video>
)

data class Video(
    val id: Id,
    val snippet: Snippet
)

data class Id(
    val videoId: String
)

data class Snippet(
    val publishedAt: String,
    val title: String,
    val thumbnails: Thumbnails,
    val description: String
) {
    val displayVideoDate = publishedAt.toDate()?.toString(Utils.VIDEO_PUBLISHED_AT_PATTERN)
}

data class Thumbnails(
    val default: Thumbnail?,
    val medium: Thumbnail?,
    val high: Thumbnail?,
    val maxres: Thumbnail?
)

data class Thumbnail(
    val url: String
)


data class VideosEndPointResultModel(
    val items: List<VideoDetails>
)

data class VideoDetails(
    val snippet: Snippet,
    val contentDetails: ContentDetails
)

data class ContentDetails(
    val duration: String
)

data class CommentThreadsEndPointResultModel(
    val nextPageToken: String?,
    val items: List<TopLevelComment>
)

data class TopLevelComment(
    val id: String,
    val snippet: CommentSnippet
)

data class CommentSnippet(
    val topLevelComment: TopLevelCommentDetails
)

data class TopLevelCommentDetails(
    val snippet: CommentDetailsSnippet
)

data class CommentDetailsSnippet(
    val authorDisplayName: String,
    val textDisplay: String,
    val publishedAt: String
) {
    val commentDate = publishedAt.toDate()?.toString(Utils.COMMENT_PUBLISHED_AT_PATTERN)
}