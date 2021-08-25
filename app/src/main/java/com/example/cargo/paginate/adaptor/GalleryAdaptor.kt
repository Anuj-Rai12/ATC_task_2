package com.example.cargo.paginate.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.cargo.data.Photo
import com.example.cargo.databinding.PhotoItemBinding
import com.example.cargo.utils.Image
import com.example.cargo.utils.PalletColor

class GalleryAdaptor constructor(
    private val context: Context,
    private val color: (PalletColor) -> Unit,
    private val image: (Image) -> Unit
) :
    PagingDataAdapter<Photo, GalViewHolder>(diffUtil) {
    override fun onBindViewHolder(holder: GalViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bindIt(photo = it, context = context, color = color, image = image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalViewHolder(binding)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }

        }
    }
}