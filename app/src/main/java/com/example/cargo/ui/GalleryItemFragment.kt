package com.example.cargo.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.viewpager2.widget.ViewPager2
import com.example.cargo.R
import com.example.cargo.TAG
import com.example.cargo.databinding.GalleryItemFragmentBinding
import com.example.cargo.paginate.adaptor.GalleryAdaptor
import com.example.cargo.utils.Image
import com.example.cargo.utils.PalletColor
import com.example.cargo.utils.SendImage
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryItemFragment : Fragment(R.layout.gallery_item_fragment) {
    private lateinit var binding: GalleryItemFragmentBinding
    private val viewModel: MaiViewModel by viewModels()
    private var galleryAdaptor: GalleryAdaptor? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GalleryItemFragmentBinding.bind(view)
        setViewPager()
        setUpUi()
        lifecycleScope.launch {
            viewModel.flow.collect {
                galleryAdaptor?.submitData(it)
            }
        }
    }

    private fun dir(title: String = "Error", msg: String = "") {
        val action =
            GalleryItemFragmentDirections.actionGlobalMyDialog(title = title, message = msg)
        findNavController().navigate(action)
    }

    private fun setUpUi() {
        galleryAdaptor?.loadStateFlow?.asLiveData()?.observe(viewLifecycleOwner) {
            when (it.append) {
                is LoadState.NotLoading -> Log.i(TAG, "setUpUi: Not Loading ..")
                is LoadState.Loading -> Log.i(TAG, "Loading..")
                is LoadState.Error -> {
                    val error = (it.append as LoadState.Error).error.localizedMessage
                    error?.let { str ->
                        dir(msg = str)
                    }
                }
            }
        }
    }

    private fun setViewPager() {
        binding.myViewpager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            galleryAdaptor = GalleryAdaptor(context = requireActivity(), {
                changeStatusBar(it)
            }, {
                imageClicked(it)
            })
            adapter = galleryAdaptor
        }
    }

    private fun imageClicked(it: Image) {
        setTransition(it.imageView, it)
    }

    private fun changeStatusBar(palletColor: PalletColor) {
        activity?.window?.statusBarColor = palletColor.darkThemColor
        (activity as AppCompatActivity?)!!.supportActionBar!!.apply {
            setBackgroundDrawable(
                ColorDrawable(palletColor.rgb)
            )
        }
    }

    private fun setTransition(galImage: ImageView, image: Image) {
        val extras =
            FragmentNavigatorExtras(galImage to getString(R.string.image_trans))
        findNavController().navigate(
            R.id.action_galleryItemFragment_to_galleryDetailFragment,
            GalleryDetailFragmentArgs(SendImage(image.bitmap)).toBundle(),
            null,
            extras
        )
    }
}