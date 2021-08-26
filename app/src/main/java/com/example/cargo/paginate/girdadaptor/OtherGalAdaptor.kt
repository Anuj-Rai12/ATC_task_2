package com.example.cargo.paginate.girdadaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.cargo.data.Photo
import com.example.cargo.databinding.PhotoItemSecondBinding
import com.example.cargo.paginate.adaptor.GalleryAdaptor
import com.example.cargo.utils.Image

class OtherGalAdaptor(private val image: (Image,Photo) -> Unit, private val context: Context) :
    PagingDataAdapter<Photo, OtherViewHolder>(GalleryAdaptor.diffUtil) {

    override fun onBindViewHolder(holder: OtherViewHolder, position: Int) {
        val curr=getItem(position)
        curr?.let {
            holder.bindIt(it,image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherViewHolder {
        val binding= PhotoItemSecondBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OtherViewHolder(binding, context)
    }
}