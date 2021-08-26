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
                }
                return@setOnMenuItemClickListener true
            }
        }
        setData()
    }

    private fun setData() {
        lifecycleScope.launchWhenStarted {
            viewModel.flow.collectLatest {
                hideLoading()
                val adaptor = if (!MainActivity.gridOrLinear)
                    gridGalleryAdaptor
                else
                    linearGalAdaptor

                adaptor?.submitData(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun linerOrGrid(choose: Int = 0) {
        when (choose) {
            0 -> {
                binding.myRecycle.isVisible = false
                binding.myViewPager.isVisible = true
                setViewPager()
            }
            else -> {
                binding.myViewPager.isVisible = false
                binding.myRecycle.isVisible = true
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
            gridGalleryAdaptor = OtherGalAdaptor({
                imageClicked(it)
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
        binding.myViewPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            linearGalAdaptor = GalleryAdaptor(context = requireActivity(), {
                changeStatusBar(it)
            }, {
                imageClicked(it)
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