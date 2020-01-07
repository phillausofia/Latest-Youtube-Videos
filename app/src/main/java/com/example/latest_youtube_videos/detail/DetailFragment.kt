package com.example.latest_youtube_videos.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.latest_youtube_videos.R

import com.example.latest_youtube_videos.databinding.FragmentDetailBinding
import com.example.latest_youtube_videos.adapter.MyRvAdapter
import com.example.latest_youtube_videos.utils.showShortToastMessage

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel

    private lateinit var binding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedVideoId = DetailFragmentArgs.fromBundle(arguments!!)
            .selectedVideoId

        val viewModelFactory = DetailViewModelFactory(selectedVideoId)

        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(DetailViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container,
            false)

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.viewModel = viewModel

        binding.recyclerViewComments.adapter = viewModel.getAdapter()

        binding.recyclerViewComments.isNestedScrollingEnabled = false

        setupErrorMessages()

        setupShowVideoDetails()

        setupShowMessageReachedEndResults()

        setupShowComments()

        setupLoadMoreComments()

    }

    private fun setupLoadMoreComments() {
        viewModel.isLoadingMoreComments.observe(this, Observer { isLoading ->
            if (isLoading) {
                (binding.recyclerViewComments.adapter as MyRvAdapter)
                    .insertProgressView()
            }

        })

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
                v, _, scrollY, _, oldScrollY ->
            v?.let {
                if (scrollY > oldScrollY) {
                    val recyclerViewHeight = it.getChildAt(it.childCount - 1).measuredHeight
                    val nestedScrollViewHeight = it.measuredHeight
                    if (scrollY >= recyclerViewHeight - nestedScrollViewHeight) {
                        if (viewModel.canGetMoreComments()) {
                            viewModel.getMoreComments()
                        }
                    }
                }
            }
        })
    }

    private fun setupShowMessageReachedEndResults() {
        viewModel.reachedEndOfCommentResults.observe(this, Observer { reachedTheEnd ->
            if (reachedTheEnd) {
                this.showShortToastMessage(resources.getString(R.string.reached_end_results))
                viewModel.doneShowingReachedTheEndResults()
            }
        })
    }

    private fun setupShowComments() {
        viewModel.selectedVideoComments.observe(this, Observer { videoComments ->
            if (videoComments.isNotEmpty()) {
                binding.recyclerViewComments.visibility = View.VISIBLE
            }
        })
    }

    private fun setupShowVideoDetails() {
        viewModel.selectedVideoDetails.observe(this, Observer { videoDetails ->
            if (videoDetails != null) {
                binding.constraintLayoutVideoDetails.visibility = View.VISIBLE
            }
        })
    }

    private fun setupErrorMessages() {
        val errorMessageObserver = Observer <String>  { errorMessage ->
            if (errorMessage != null) {
                binding.textViewEmptyView.text = errorMessage
                binding.textViewEmptyView.visibility = View.VISIBLE
                binding.recyclerViewComments.visibility = View.GONE
                binding.constraintLayoutVideoDetails.visibility = View.GONE
            }
        }

        viewModel.errorRetrievingVideoDetails.observe(this, errorMessageObserver)
        viewModel.errorRetrievingVideoComments.observe(this, errorMessageObserver)
    }
}
