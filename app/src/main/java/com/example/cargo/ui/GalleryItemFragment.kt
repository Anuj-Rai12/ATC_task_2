package com.example.cargo.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
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
    private var otherGalAdaptor: OtherGalAdaptor? = null

    @Inject
    lateinit var customProgress: CustomProgress

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GalleryItemFragmentBinding.bind(view)
        showLoading()
        setPotherAdaptor()
        changeStatusBar(null)
        MainActivity.toolbar?.let {
            it.inflateMenu(R.menu.icon_tab_list)
            it.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.grid_layout -> {
                        showLoading()
                        changeStatusBar(null)
                        galleryAdaptor = null
                        binding.myViewPager.isVisible = false
                        binding.myRecycle.isVisible = true
                        setPotherAdaptor()
                        lifecycleScope.launchWhenStarted {
                            viewModel.flow.collectLatest { photo ->
                                hideLoading()
                                otherGalAdaptor?.submitData(photo)
                            }
                        }
                    }
                    R.id.line_layout -> {
                        showLoading()
                        otherGalAdaptor = null
                        binding.myRecycle.isVisible = false
                        binding.myViewPager.isVisible = true
                        setRecycleView()
                        lifecycleScope.launchWhenStarted {
                            viewModel.flow.collectLatest { photo ->
                                hideLoading()
                                galleryAdaptor?.submitData(photo)
                            }
                        }
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.flow.collectLatest {
                customProgress.hideLoading()
                otherGalAdaptor?.submitData(it)
            }
        }
    }

    private fun showLoading() =
        customProgress.showLoading(requireActivity(), getString(R.string.page_loading))

    private fun hideLoading() = customProgress.hideLoading()

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPotherAdaptor() {
        binding.myRecycle.apply {
            setHasFixedSize(true)
            setBackgroundColor(getColor(R.color.app_color))
            layoutManager = GridLayoutManager(requireContext(), 2)
            otherGalAdaptor = OtherGalAdaptor({
                imageClicked(it)
            }, requireActivity())
            adapter = otherGalAdaptor?.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor {
                    otherGalAdaptor!!::retry
                },
                header = LoadingFooterAndHeaderAdaptor {
                    otherGalAdaptor!!::retry
                }
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setRecycleView() {
        binding.myViewPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
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
        activity?.window?.statusBarColor = palletColor?.darkThemColor ?: getColor(R.color.app_color)
        MainActivity.toolbar?.let {
            it.setTitleTextColor(palletColor?.titleTextColor ?: getColor(R.color.white))
            it.setBackgroundColor(palletColor?.rgb ?: getColor(R.color.app_color))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getColor(color: Int) = resources.getColor(color, null)

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