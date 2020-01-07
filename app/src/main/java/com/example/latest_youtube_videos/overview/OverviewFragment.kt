package com.example.latest_youtube_videos.overview

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.latest_youtube_videos.R
import com.example.latest_youtube_videos.databinding.FragmentOverviewBinding
import com.example.latest_youtube_videos.adapter.MyRvAdapter
import com.example.latest_youtube_videos.utils.shouldLoadMoreResults
import com.example.latest_youtube_videos.utils.showShortToastMessage

class OverviewFragment : Fragment() {


    private lateinit var viewModel: OverviewViewModel

    private lateinit var binding: FragmentOverviewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
            container, false)

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerViewVideos.adapter = viewModel.getAdapter()

        setupGettingFirstSetOfVideos()

        setupShowingResults()

        setupReachedEndOfResults()

        setupErrorMessage()

        setupNavigationToDetailFragment()

        setupLoadMoreResults()

        setYoutubeChannelId()

    }

    private fun setYoutubeChannelId() {
        binding.buttonShowVideos.setOnClickListener {
            val youtubeChannelId = getUserInput()
            if (youtubeChannelId.isNotEmpty() && youtubeChannelId.isNotBlank()) {
                viewModel.setYoutubeChannelId(youtubeChannelId)
            } else {
                this.showShortToastMessage(resources.getString(R.string.invalid_channel_id_message))
            }
        }
    }

    private fun getUserInput() = binding.textViewChannelId.text.toString()

    private fun setupGettingFirstSetOfVideos() {
        viewModel.youtubeChannelId.observe(this, Observer { channelId ->
            if (!channelId.isNullOrEmpty()) {
                viewModel.getFirstSetOfVideos()
                setVisibilityGone(binding.constraintLayoutInput)
                setVisibilityVisible(binding.constraintLayoutOverview)
            }
        })
    }

    private fun setupReachedEndOfResults() {
        viewModel.reachedEndOfVideoResults.observe(this, Observer { reachedTheEnd ->
            if (reachedTheEnd) {
                this.showShortToastMessage(resources.getString(R.string.reached_end_results))
                viewModel.doneShowingReachedTheEndResults()
            }
        })
    }

    private fun setupErrorMessage() {
        viewModel.errorRetrievingVideos.observe(this, Observer { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                setVisibilityGone(binding.mainProgressBar, binding.recyclerViewVideos)
                setVisibilityVisible(binding.emptyView)
                binding.emptyView.text = errorMessage
            }
        })
    }

    private fun setupShowingResults() {
        viewModel.videos.observe(this, Observer { videos ->
            if (videos.isNotEmpty()) {
                setVisibilityGone(binding.mainProgressBar)
                setVisibilityVisible(binding.recyclerViewVideos)
            }
        })
    }

    private fun setupNavigationToDetailFragment() {
        viewModel.navigateToSelectedVideo.observe(this, Observer { selectedVideo ->
            if (selectedVideo != null) {
                this.findNavController().navigate(OverviewFragmentDirections
                    .actionShowVideoDetails(selectedVideo))
                viewModel.displayVideoDetailsComplete()
            }
        })
    }

    private fun setupLoadMoreResults() {
        viewModel.isLoadingMoreVideos.observe(this, Observer { isLoading ->
            if (isLoading) {
                (binding.recyclerViewVideos.adapter as MyRvAdapter)
                    .insertProgressView()
            }

        })

        binding.recyclerViewVideos.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val linearLayoutManager =
                        binding.recyclerViewVideos.layoutManager as LinearLayoutManager
                    if (linearLayoutManager.shouldLoadMoreResults()) {
                        if (viewModel.canGetMoreResults()) {
                            viewModel.getMoreVideos()
                        }
                    }
                }
            }
        })
    }

    private fun setVisibilityVisible(vararg views: View) {
        views.forEach {
            it.visibility = View.VISIBLE
        }
    }

    private fun setVisibilityGone(vararg views: View) {
        views.forEach {
            it.visibility = View.GONE
        }
    }

}
