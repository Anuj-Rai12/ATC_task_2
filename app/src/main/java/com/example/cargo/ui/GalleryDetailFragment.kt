package com.example.cargo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cargo.R
import com.example.cargo.databinding.GalleryDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryDetailFragment :Fragment(R.layout.gallery_detail_fragment){
    private lateinit var binding: GalleryDetailFragmentBinding
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= GalleryDetailFragmentBinding.bind(view)
        binding.galImageFull.setBackgroundColor(resources.getColor(R.color.black))
    }
}