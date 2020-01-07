package com.example.latest_youtube_videos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.latest_youtube_videos.R
import com.example.latest_youtube_videos.databinding.ListItemCommentBinding
import com.example.latest_youtube_videos.databinding.ListItemViewBinding
import com.example.latest_youtube_videos.network.TopLevelComment
import com.example.latest_youtube_videos.network.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_VIDEO_ITEM = 0
private const val ITEM_VIEW_TYPE_PROGRESS_ITEM = 1
private const val ITEM_VIEW_TYPE_COMMENT_ITEM = 2

class MyRvAdapter(private val adapterClickListener: AdapterOnClickListener? = null):
    ListAdapter<DataItem, RecyclerView.ViewHolder>(
        DiffCallBack()
    ) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var items = mutableListOf<DataItem>()

    fun addDataItemsAndSubmitList(list: List<DataItem>) {
        adapterScope.launch {
            items = list.toMutableList()
            withContext(Dispatchers.Main) {
                submitList(items)
            }

        }
    }


    fun insertProgressView() {
        if (items.indexOf(DataItem.ProgressItem) == -1) {
            items.add(DataItem.ProgressItem)
            notifyItemInserted(items.size)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_COMMENT_ITEM -> CommentViewHolder.from(parent)
            ITEM_VIEW_TYPE_VIDEO_ITEM -> VideoViewHolder.from(parent)
            else -> ProgressViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> {
                val videoItem = getItem(position) as DataItem.VideoItem
                holder.itemView.setOnClickListener {
                    adapterClickListener?.onClick(videoItem.video)
                }
                holder.bind(videoItem.video)
            }
            is CommentViewHolder -> {
                val commentItem = getItem(position) as DataItem.CommentItem
                holder.bind(commentItem.comment)
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.VideoItem -> ITEM_VIEW_TYPE_VIDEO_ITEM
            is DataItem.ProgressItem -> ITEM_VIEW_TYPE_PROGRESS_ITEM
            is DataItem.CommentItem -> ITEM_VIEW_TYPE_COMMENT_ITEM
        }
    }

    class VideoViewHolder(private val binding: ListItemViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {
            binding.video = video
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): VideoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemViewBinding.inflate(layoutInflater,
                    parent, false)
                return VideoViewHolder(
                    binding
                )
            }
        }
    }

    class CommentViewHolder(private val binding: ListItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: TopLevelComment) {
            binding.comment = comment
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CommentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCommentBinding.inflate(layoutInflater,
                    parent, false)
                return CommentViewHolder(
                    binding
                )
            }
        }
    }

    class ProgressViewHolder(view: View): RecyclerView.ViewHolder(view) {

        companion object {
            fun from(parent: ViewGroup): ProgressViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.progress_item, parent,
                    false)
                return ProgressViewHolder(
                    view
                )
            }
        }
    }
    class DiffCallBack: DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class AdapterOnClickListener(val myClickListener: (video: Video) -> Unit) {
        fun onClick(video: Video) = myClickListener(video)
    }
}

sealed class DataItem {

    data class VideoItem(val video: Video): DataItem() {
        override val id = video.id.videoId
    }

    data class CommentItem(val comment: TopLevelComment): DataItem() {
        override val id = comment.id
    }

    object ProgressItem: DataItem() {
        override val id = "Progress item"
    }

    abstract val id: String
}