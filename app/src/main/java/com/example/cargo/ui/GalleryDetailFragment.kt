package com.example.cargo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cargo.R
import com.example.cargo.databinding.GalleryDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryDetailFragment :Fragment(R.layout.gallery_detail_fragment){
    private lateinit var binding: GalleryDetailFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= GalleryDetailFragmentBinding.bind(view)
        binding.galImageFull.setOnClickListener {
            val action=GalleryDetailFragmentDirections.actionGlobalMyDialog(title = "Hello","I'm Anuj")
            findNavController().navigate(action)
        }
    }
}