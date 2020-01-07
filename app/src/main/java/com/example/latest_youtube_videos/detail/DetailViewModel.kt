package com.example.latest_youtube_videos.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.latest_youtube_videos.utils.Utils
import com.example.latest_youtube_videos.network.TopLevelComment
import com.example.latest_youtube_videos.network.VideoDetails
import com.example.latest_youtube_videos.network.YoutubeApi
import com.example.latest_youtube_videos.adapter.MyRvAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.Exception

class DetailViewModel(private val gmbnVideoId: String): ViewModel() {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _adapter = MyRvAdapter()

    private val _selectedVideoDetails = MutableLiveData<VideoDetails>()
    val selectedVideoDetails: LiveData<VideoDetails>
        get() = _selectedVideoDetails

    private val _reachedEndOfCommentResults = MutableLiveData<Boolean>(false)
    val reachedEndOfCommentResults: LiveData<Boolean>
        get() = _reachedEndOfCommentResults

    private val _selectedVideoComments = MutableLiveData<List<TopLevelComment>>()
    val selectedVideoComments: LiveData<List<TopLevelComment>>
        get() = _selectedVideoComments

    private val _isLoadingMoreComments = MutableLiveData<Boolean>(false)
    val isLoadingMoreComments: LiveData<Boolean>
        get() = _isLoadingMoreComments

    private val _errorRetrievingVideoDetails = MutableLiveData<String>()
    val errorRetrievingVideoDetails: LiveData<String>
        get() = _errorRetrievingVideoDetails

    private val _errorRetrievingComments = MutableLiveData<String>()
    val errorRetrievingVideoComments: LiveData<String>
        get() = _errorRetrievingComments

    private var maxResultsComments = Utils.MAX_RESULTS

    private var nextPageToken: String? = null

    init {
        getVideoDetails()
        getVideoComments()
    }

    private fun getVideoDetails() {

        viewModelScope.launch {
            try {
                val result = YoutubeApi.youtubeApiService
                    .getVideoDetails(gmbnVideoId).await()
                _selectedVideoDetails.value = result.items.first()
            } catch (e: Exception) {
                _errorRetrievingVideoDetails.value = e.message
            }
        }

    }

    /*
    The API will always return the same number of results, to retrieve more result we'll use
    a token to get the next 10 results, for example, that we append to the old results. The initial
    call is made without a token.
     */
    private fun getVideoComments() {
        viewModelScope.launch {
            try {
                val result = YoutubeApi.youtubeApiService.getTopLevelComments(
                    videoId = gmbnVideoId, maxResults = maxResultsComments, nextPageToken =
                    nextPageToken).await()
                _selectedVideoComments.value = when (nextPageToken) {
                    null -> result.items
                    else -> _selectedVideoComments.value!!.toMutableList() + result.items
                }
                nextPageToken = result.nextPageToken
            } catch (e: Exception) {
                _errorRetrievingComments.value = e.message
            }
            _isLoadingMoreComments.value = false
        }
    }

    fun getMoreComments() {
        when (nextPageToken) {
            null -> _reachedEndOfCommentResults.value = true
            else -> {
                _isLoadingMoreComments.value = true
                getVideoComments()
            }
        }
    }

    fun getAdapter() = _adapter

    fun doneShowingReachedTheEndResults() {
        _reachedEndOfCommentResults.value = false
    }

    fun canGetMoreComments() =
        _isLoadingMoreComments.value == false && _reachedEndOfCommentResults.value == false

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}
