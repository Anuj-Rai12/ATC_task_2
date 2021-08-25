package com.example.cargo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cargo.R
import com.example.cargo.databinding.GalleryItemFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryItemFragment :Fragment(R.layout.gallery_item_fragment){
    private lateinit var binding: GalleryItemFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= GalleryItemFragmentBinding.bind(view)
        binding.galImage.setOnClickListener {
            val action=GalleryItemFragmentDirections.actionGalleryItemFragmentToGalleryDetailFragment()
            findNavController().navigate(action)
        }
    }
}