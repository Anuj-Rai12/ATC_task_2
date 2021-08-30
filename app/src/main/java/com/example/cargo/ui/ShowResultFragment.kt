package com.example.cargo.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cargo.MainActivity
import com.example.cargo.R
import com.example.cargo.TAG
import com.example.cargo.data.Photo
import com.example.cargo.databinding.ShowResultFragmentBinding
import com.example.cargo.paginate.erroradaptor.LoadingFooterAndHeaderAdaptor
import com.example.cargo.paginate.girdadaptor.OtherGalAdaptor
import com.example.cargo.utils.*
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShowResultFragment : Fragment(R.layout.show_result_fragment) {
    private lateinit var binding: ShowResultFragmentBinding
    private val viewModel: MaiViewModel by viewModels()
    private var gridGalleryAdaptor: OtherGalAdaptor? = null
    private val args: ShowResultFragmentArgs by navArgs()
    @Inject
    lateinit var customProgress: CustomProgress

    @Inject
    lateinit var networkUtils: NetworkUtils

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ShowResultFragmentBinding.bind(view)
        showLoading()
        utils()
        setUpRecycleView()
        getSearchData(args.title)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun utils() {
        activity?.window?.statusBarColor = getColor(R.color.app_color)
        MainActivity.toolbar?.let {
            it.menu?.clear()
            it.setTitleTextColor(getColor(R.color.white))
            it.setBackgroundColor(getColor(R.color.app_color))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpRecycleView() {
        binding.searchResult.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            gridGalleryAdaptor = OtherGalAdaptor({ image, photo ->
                setTransition(image = image, photo = photo, galImage = image.imageView)
            }, requireActivity())
            adapter = gridGalleryAdaptor?.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor({
                    gridGalleryAdaptor?.retry()
                }, { error ->
                    dir(message = error)
                    msg()
                }),
                header = LoadingFooterAndHeaderAdaptor({
                    gridGalleryAdaptor?.retry()
                }, { error ->
                    dir(message = error)
                    msg()
                })
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ShowToast")
    private fun getSearchData(query: String) {
        viewModel.apply {
            Log.i(TAG, "getSearchData: NO InterNet ? = ${networkUtils.isConnected()}")
            if (networkUtils.isConnected()) {
                lifecycleScope.launch {
                    getSearchQuery(query).collectLatest {
                        hideLoading()
                        gridGalleryAdaptor?.submitData(it)
                    }
                }
            } else {
                hideLoading()
                requireActivity().msg(title = "Please Connect To Internet") {
                    lifecycleScope.launch {
                        getSearchQuery(query).collectLatest {
                            gridGalleryAdaptor?.submitData(it)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() =
        customProgress.showLoading(requireActivity(), getString(R.string.page_loading))

    private fun hideLoading() = customProgress.hideLoading()

    private fun dir(
        title: String = "Network Error",
        message: String = "UnWanted Error",
    ) {
        val action = ShowResultFragmentDirections.actionGlobalMyDialog(title, message)
        findNavController().navigate(action)
    }

    private fun setTransition(galImage: ImageView, image: Image, photo: Photo) {
        val extras =
            FragmentNavigatorExtras(galImage to getString(R.string.image_trans))
        findNavController().navigate(
            R.id.action_showResultFragment_to_galleryDetailFragment,
            GalleryDetailFragmentArgs(SendImage(image.bitmap), photo).toBundle(),
            null,
            extras
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getColor(color: Int) = resources.getColor(color, null)

    @RequiresApi(Build.VERSION_CODES.M)
    private fun msg() = requireActivity().msg("Network Error") {
        gridGalleryAdaptor?.retry()
    }
}