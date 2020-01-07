package com.example.latest_youtube_videos.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.latest_youtube_videos.utils.Utils
import com.example.latest_youtube_videos.network.Video
import com.example.latest_youtube_videos.network.YoutubeApi
import com.example.latest_youtube_videos.adapter.MyRvAdapter
import kotlinx.coroutines.*

class OverviewViewModel : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _adapter =
        MyRvAdapter(MyRvAdapter.AdapterOnClickListener {
            displayVideoDetails(it)
        })

    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>>
        get() = _videos

    private val _navigateToSelectedVideo = MutableLiveData<String>()
    val navigateToSelectedVideo: LiveData<String>
        get() = _navigateToSelectedVideo

    private var nextPageToken: String? = null

    private val maxResults = Utils.MAX_RESULTS

    private val _youtubeChannelId = MutableLiveData<String>()
    val youtubeChannelId: LiveData<String>
        get() = _youtubeChannelId

    private val _isLoadingMoreVideos = MutableLiveData<Boolean>(false)
    val isLoadingMoreVideos: LiveData<Boolean>
        get() = _isLoadingMoreVideos
    
    private val _errorRetrievingVideos = MutableLiveData<String>()
    val errorRetrievingVideos: LiveData<String>
        get() = _errorRetrievingVideos

    private val _reachedEndOfVideoResults = MutableLiveData<Boolean>(false)
    val reachedEndOfVideoResults: LiveData<Boolean>
        get() = _reachedEndOfVideoResults


    /*
    The API will return the same amount of results, if available, so to get the next
    10 results, for example, we just change the next page token returned by the API.
    We append the new results to the old ones. If the token returned is null, then
    we have reached the end of results.
     */
    private fun getVideos() {
        viewModelScope.launch {
            val resultDeferred = YoutubeApi
                .youtubeApiService.getGmbnVideos(maxResults = maxResults,
                nextPageToken = nextPageToken, channelId = _youtubeChannelId.value!!)
            try {
                val result = resultDeferred.await()
                _videos.value = when (nextPageToken) {
                    null -> result.items
                    else -> _videos.value!!.toMutableList() + result.items
                }
                nextPageToken = result.nextPageToken
                _isLoadingMoreVideos.value = false
            } catch (e: Exception) {
                _errorRetrievingVideos.value = e.message
                }
            _isLoadingMoreVideos.value = false

        }
    }


    fun getMoreVideos() {
        when (nextPageToken) {
            null -> _reachedEndOfVideoResults.value = true
            else -> {
                _isLoadingMoreVideos.value = true
                getVideos()
            }
        }
    }

    private fun displayVideoDetails(selectedVideo: Video) {
        _navigateToSelectedVideo.value = selectedVideo.id.videoId
    }

    fun doneShowingReachedTheEndResults() {
        _reachedEndOfVideoResults.value = false
    }

    fun displayVideoDetailsComplete() {
        _navigateToSelectedVideo.value = null
    }

    fun setYoutubeChannelId(id: String) {
        _youtubeChannelId.value = id
    }

    fun getFirstSetOfVideos() {
        getVideos()
    }

    fun getAdapter() = _adapter

    fun canGetMoreResults() =
        _isLoadingMoreVideos.value == false && _reachedEndOfVideoResults.value == false

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}
