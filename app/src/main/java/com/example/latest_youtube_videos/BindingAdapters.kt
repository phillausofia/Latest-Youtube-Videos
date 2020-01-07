package com.example.latest_youtube_videos

import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.example.latest_youtube_videos.network.Thumbnails
import com.example.latest_youtube_videos.network.TopLevelComment
import com.example.latest_youtube_videos.network.Video
import com.example.latest_youtube_videos.adapter.DataItem
import com.example.latest_youtube_videos.adapter.MyRvAdapter
import com.example.latest_youtube_videos.network.CommentDetailsSnippet
import com.example.latest_youtube_videos.utils.UtilsFunctions

@BindingAdapter("videosList")
fun bindVideosList(recyclerView: RecyclerView, data: List<Video>?) {
    val adapter = recyclerView.adapter as MyRvAdapter
    if (data != null && data.size != adapter.itemCount) {
        adapter.addDataItemsAndSubmitList(data.map { DataItem.VideoItem(it)})
    }
}

@BindingAdapter("commentsList")
fun bindCommentsList(recyclerView: RecyclerView, data: List<TopLevelComment>?) {
    val adapter = recyclerView.adapter as MyRvAdapter
    if (data != null && data.size != adapter.itemCount) {
        adapter.addDataItemsAndSubmitList(data.map { DataItem.CommentItem(it) })
    }
}

@BindingAdapter("thumbnails")
fun ImageView.bindThumbnail(thumbnails: Thumbnails?) {

    thumbnails?.let {
        val imgUrl = thumbnails.maxres?.url ?: thumbnails.medium!!.url
        val thumbnailUri = imgUrl.toUri()
            .buildUpon().scheme("https").build()

        GlideApp.with(context).load(thumbnailUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(this)
    }
}

@BindingAdapter("string")
fun TextView.bindString(string: String?) {
    string?.let {
        text = when {
            it.contains("&#39;") -> {
                if (Build.VERSION.SDK_INT >= 24) {
                    Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(it)
                }
            }
            else -> it
        }
    }
}

@BindingAdapter("videoDate")
fun TextView.bindVideoDate(videoDate: String?) {
    videoDate?.let {
        text = String.format(resources.getString(R.string.published_at), videoDate)
    }
}

@BindingAdapter("commentAuthorDate")
fun TextView.bindAuthorDate(snippet: CommentDetailsSnippet?) {
    snippet?.let {
        text = String.format(resources.getString(R.string.author_wrote), it.authorDisplayName,
            it.commentDate)
    }
}

@BindingAdapter("videoDuration")
fun TextView.bindVideoDuration(duration: String?) {
    duration?.let {
        val (hours, mins, secs) = UtilsFunctions.extractHoursMinutesSeconds(duration)
        text =  if (hours == 0) {
            String.format(resources.getString(R.string.duration_m_s), mins, secs)
        } else {
            String.format(resources.getString(R.string.duration_h_m_s), hours, mins, secs)
        }
    }
}


