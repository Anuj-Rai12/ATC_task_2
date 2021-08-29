package com.example.cargo.ui

import android.os.Build
import android.os.Bundle
import android.view.*
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
import com.example.cargo.data.Photo
import com.example.cargo.databinding.GalleryItemFragmentBinding
import com.example.cargo.paginate.adaptor.GalleryAdaptor
import com.example.cargo.paginate.erroradaptor.LoadingFooterAndHeaderAdaptor
import com.example.cargo.paginate.girdadaptor.OtherGalAdaptor
import com.example.cargo.utils.*
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class GalleryItemFragment : Fragment(R.layout.gallery_item_fragment) {
    private lateinit var binding: GalleryItemFragmentBinding
    private val viewModel: MaiViewModel by viewModels()
    private var linearGalAdaptor: GalleryAdaptor? = null
    private var gridGalleryAdaptor: OtherGalAdaptor? = null

    @Inject
    lateinit var customProgress: CustomProgress

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GalleryItemFragmentBinding.bind(view)
        showLoading()
        if (!MainActivity.gridOrLinear) {
            linerOrGrid(2)
        } else
            linerOrGrid()

        MainActivity.toolbar?.menu?.clear()
        MainActivity.toolbar?.let {
            it.inflateMenu(R.menu.icon_tab_list)
            it.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.grid_layout -> {
                        linearGalAdaptor = null
                        linerOrGrid(2)
                        MainActivity.gridOrLinear = false
                        setData()
                    }
                    R.id.line_layout -> {
                        showLoading()
                        gridGalleryAdaptor = null
                        linerOrGrid()
                        MainActivity.gridOrLinear = true
                        setData()
                    }
                    R.id.my_search_btn -> {
                        val action =
                            GalleryItemFragmentDirections.actionGalleryItemFragmentToSearchFragment()
                        findNavController().navigate(action)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
        setData()
    }

    private fun setData() {
        lifecycleScope.launchWhenStarted {
            if (viewModel.internConnected) {
                viewModel.flow.collectLatest {
                    hideLoading()
                    val adaptor = if (!MainActivity.gridOrLinear)
                        gridGalleryAdaptor
                    else
                        linearGalAdaptor

                    adaptor?.submitData(it)
                }
            } else {
                val action = GalleryItemFragmentDirections.actionGlobalMyDialog(
                    "No Internet",
                    "Please Connect to InterNet !!"
                )
                findNavController().navigate(action)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun linerOrGrid(choose: Int = 0) {
        when (choose) {
            0 -> setViewPager()
            else -> {
                setGirdAdaptor()
                changeStatusBar(null)
            }
        }
    }

    private fun showLoading() =
        customProgress.showLoading(requireActivity(), getString(R.string.page_loading))

    private fun hideLoading() = customProgress.hideLoading()

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setGirdAdaptor() {
        binding.myRecycle.apply {
            setHasFixedSize(true)
            setBackgroundColor(getColor(R.color.app_color))
            layoutManager = GridLayoutManager(requireContext(), 2)
            gridGalleryAdaptor = OtherGalAdaptor({ image, photo ->
                imageClicked(image, photo)
            }, requireActivity())
            adapter = gridGalleryAdaptor?.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor {
                    gridGalleryAdaptor!!::retry
                },
                header = LoadingFooterAndHeaderAdaptor {
                    gridGalleryAdaptor!!::retry
                }
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setViewPager() {
        binding.myRecycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            linearGalAdaptor = GalleryAdaptor(context = requireActivity(), {
                changeStatusBar(it)
            }, { image, photo ->
                imageClicked(image, photo)
            })
            adapter = linearGalAdaptor?.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor {
                    linearGalAdaptor!!::retry
                },
                header = LoadingFooterAndHeaderAdaptor {
                    linearGalAdaptor!!::retry
                }
            )
        }
    }

    private fun imageClicked(it: Image, photo: Photo) {
        setTransition(it.imageView, it, photo)
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

    private fun setTransition(galImage: ImageView, image: Image, photo: Photo) {
        val extras =
            FragmentNavigatorExtras(galImage to getString(R.string.image_trans))
        findNavController().navigate(
            R.id.action_galleryItemFragment_to_galleryDetailFragment,
            GalleryDetailFragmentArgs(SendImage(image.bitmap), photo).toBundle(),
            null,
            extras
        )
    }
}