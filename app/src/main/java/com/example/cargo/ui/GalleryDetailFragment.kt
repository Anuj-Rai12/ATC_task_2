package com.example.cargo.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.example.cargo.MainActivity
import com.example.cargo.R
import com.example.cargo.databinding.GalleryDetailFragmentBinding
import com.example.cargo.utils.checkFieldValue
import com.example.cargo.utils.manipulateColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryDetailFragment : Fragment(R.layout.gallery_detail_fragment) {
    private lateinit var binding: GalleryDetailFragmentBinding
    private val args: GalleryDetailFragmentArgs by navArgs()
    private var imageTitle: String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GalleryDetailFragmentBinding.bind(view)
        binding.galImageFull.setImageBitmap(args.image.bitmap)
        createPaletteAsync(args.image.bitmap)
        imageTitle = if (!checkFieldValue(args.photo.title))
            "@ ${args.photo.title}"
        else
            "@ Beautiful"
        getTransition()
        MainActivity.toolbar?.menu?.clear()
        MainActivity.toolbar?.let {
            it.inflateMenu(R.menu.detail_screen)
            it.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.detail_screen -> {
                        val action =
                            GalleryDetailFragmentDirections.actionGlobalMyDialog(
                                title = imageTitle!!,
                                message = msg()
                            )
                        findNavController().navigate(action)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun msg() = "Id -> ${args.photo.id}\n" +
            "Size -> ${args.photo.widthS}'' * ${args.photo.heightS}'' \n" +
            "Title -> $imageTitle\n" +
            "URL -> ${args.photo.urlS}\n"

    private fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            palette?.vibrantSwatch?.let { swatch ->
                val rbg = swatch.rgb
                val darkTheme = manipulateColor(rbg, 0.8.toFloat())
                binding.galImageFull.setBackgroundColor(rbg)
                activity?.window?.statusBarColor = darkTheme
                MainActivity.toolbar?.let {
                    it.setTitleTextColor(swatch.titleTextColor)
                    it.setBackgroundColor(rbg)
                }
            }
        }
    }

    private fun getTransition() {
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }
}