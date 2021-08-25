package com.example.cargo.ui

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.cargo.R
import com.example.cargo.databinding.GalleryDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryDetailFragment : Fragment(R.layout.gallery_detail_fragment) {
    private lateinit var binding: GalleryDetailFragmentBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GalleryDetailFragmentBinding.bind(view)
        getTransition()
        binding.galImageFull.setBackgroundColor(resources.getColor(R.color.black, null))
    }
    private fun getTransition(){
        val animation = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }
}