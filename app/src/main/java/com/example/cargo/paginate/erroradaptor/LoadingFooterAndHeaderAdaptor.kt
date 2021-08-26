package com.example.cargo.paginate.erroradaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargo.databinding.ErrorLayoutBinding

class LoadingFooterAndHeaderAdaptor(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingFooterAndHeaderAdaptor.LoadingViewHolder>() {

    inner class LoadingViewHolder(
        private val binding: ErrorLayoutBinding,
        private val retry: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.apply {
                errorTxt.text = loadState.toString()
                progressBar.isVisible = loadState is LoadState.Loading
                retryBtn.isVisible = loadState !is LoadState.Loading
                errorTxt.isVisible = loadState !is LoadState.Loading
            }
        }

        init {
            binding.retryBtn.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = ErrorLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }
}