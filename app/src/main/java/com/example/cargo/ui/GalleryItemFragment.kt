package com.example.cargo.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargo.MainActivity
import com.example.cargo.R
import com.example.cargo.databinding.GalleryItemFragmentBinding
import com.example.cargo.paginate.adaptor.GalleryAdaptor
import com.example.cargo.paginate.erroradaptor.LoadingFooterAndHeaderAdaptor
import com.example.cargo.paginate.girdadaptor.OtherGalAdaptor
import com.example.cargo.utils.CustomProgress
import com.example.cargo.utils.Image
import com.example.cargo.utils.PalletColor
import com.example.cargo.utils.SendImage
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class GalleryItemFragment : Fragment(R.layout.gallery_item_fragment) {
    private lateinit var binding: GalleryItemFragmentBinding
    private val viewModel: MaiViewModel by viewModels()
    private var galleryAdaptor: GalleryAdaptor? = null
    private lateinit var otherGalAdaptor: OtherGalAdaptor

    @Inject
    lateinit var customProgress: CustomProgress

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GalleryItemFragmentBinding.bind(view)
        customProgress.showLoading(requireActivity(), getString(R.string.page_loading))
        //setRecycleView()
        changeStatusBar(null)
        setPotherAdaptor()
        lifecycleScope.launchWhenStarted {
            viewModel.flow.collectLatest {
                customProgress.hideLoading()
                otherGalAdaptor.submitData(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPotherAdaptor() {
        binding.myRecycle.apply {
            setHasFixedSize(true)
            setBackgroundColor(resources.getColor(R.color.app_color,null))
            layoutManager = GridLayoutManager(requireContext(), 2)
            otherGalAdaptor = OtherGalAdaptor({
                imageClicked(it)
            }, requireActivity())
            adapter = otherGalAdaptor.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor {
                    otherGalAdaptor::retry
                },
                header = LoadingFooterAndHeaderAdaptor {
                    otherGalAdaptor::retry
                }
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setRecycleView() {
        binding.myRecycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            galleryAdaptor = GalleryAdaptor(context = requireActivity(), {
                changeStatusBar(it)
            }, {
                imageClicked(it)
            })
            adapter = galleryAdaptor?.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor {
                    galleryAdaptor!!::retry
                },
                header = LoadingFooterAndHeaderAdaptor {
                    galleryAdaptor!!::retry
                }
            )
        }
    }

    private fun imageClicked(it: Image) {
        setTransition(it.imageView, it)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeStatusBar(palletColor: PalletColor?) {
        activity?.window?.statusBarColor = resources.getColor(R.color.app_color,null)
        MainActivity.toolbar?.let {
            it.setTitleTextColor(resources.getColor(R.color.white,null))
            it.setBackgroundColor(resources.getColor(R.color.app_color,null))
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