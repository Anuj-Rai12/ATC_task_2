package com.example.cargo.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cargo.MainActivity
import com.example.cargo.R
import com.example.cargo.TAG
import com.example.cargo.data.Photo
import com.example.cargo.databinding.FragmentSearchBinding
import com.example.cargo.paginate.erroradaptor.LoadingFooterAndHeaderAdaptor
import com.example.cargo.paginate.girdadaptor.OtherGalAdaptor
import com.example.cargo.utils.*
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private var searchRes: SearchView? = null
    private val viewModel: MaiViewModel by viewModels()
    private val disposables = CompositeDisposable()
    private var gridGalleryAdaptor: OtherGalAdaptor? = null

    @Inject
    lateinit var customProgress: CustomProgress

    @Inject
    lateinit var networkUtils: NetworkUtils

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        toolBarSearch()
        val observable = Observable.create<String> { emitter ->
            searchRes?.onQueasyListenerChanged { query ->
                if (!emitter.isDisposed) {
                    emitter.onNext(query)
                }
            }
        }.debounce(FileUtils.timeToSearch.toLong(), TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
        observed(observable)
        viewModel.searchQuery.asLiveData().observe(viewLifecycleOwner) {
            if (it.isNotBlank() && it.isNotEmpty() && !it.isNullOrBlank()) {
                MainActivity.toolbar?.title = it
                searchRes?.setQuery(it, false)
                setUpRecycleView()
                getSearchData(query = it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpRecycleView() {
        binding.searchResult.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            gridGalleryAdaptor = OtherGalAdaptor({ image, photo ->
                dir(choose = 21, image = image, photo = photo)
            }, requireActivity())
            adapter = gridGalleryAdaptor?.withLoadStateHeaderAndFooter(
                footer = LoadingFooterAndHeaderAdaptor({
                    gridGalleryAdaptor?.retry()
                }, { error ->
                    dir(title = "Network Error", message = error)
                    msg()
                }),
                header = LoadingFooterAndHeaderAdaptor({
                    gridGalleryAdaptor?.retry()
                }, { error ->
                    dir(title = "Network Error", message = error)
                    msg()
                })
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun msg() = requireActivity().msg("Network Error") {
        gridGalleryAdaptor?.retry()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ShowToast")
    private fun getSearchData(query: String) {
        showLoading()
        viewModel.apply {
            Log.i(TAG, "getSearchData: NO InterNet ? = ${networkUtils.isConnected()}")
            if (networkUtils.isConnected()) {
                binding.searchImage.hide()
                binding.searchResult.show()
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
    private fun observed(observable: @NonNull Observable<String>) {
        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable?) {
                d?.let {
                    disposables.add(it)
                    Log.i(TAG, "onSubscribe: Disposable Is Added")
                }
            }

            override fun onNext(s: String) {
                if (s.isBlank() || s.isEmpty())
                    return
                viewModel.searchQuery.value = s
            }

            override fun onError(e: Throwable?) {
                e?.localizedMessage?.let {
                    dir(message = it)
                }
            }

            override fun onComplete() {
                Log.i(TAG, "onComplete: Work Completed")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toolBarSearch() {
        MainActivity.toolbar?.let { toolbar ->
            toolbar.menu?.clear()
            toolbar.inflateMenu(R.menu.scr_menu)
            activity?.window?.statusBarColor = getColor(R.color.app_color)
            MainActivity.toolbar?.let {
                it.setTitleTextColor(getColor(R.color.white))
                it.setBackgroundColor(getColor(R.color.app_color))
            }
            val searchViews = toolbar.menu.findItem(R.id.my_search_view)
            searchRes = searchViews?.actionView as SearchView
            setUpSearch(searchViews)
        }
    }

    private fun dir(
        choose: Int = 0,
        title: String = "Error",
        message: String = "UnWanted Error",
        image: Image? = null,
        photo: Photo? = null,
    ) {
        val action = when (choose) {
            0 -> SearchFragmentDirections.actionGlobalMyDialog(title, message)
            else -> {
                val sendImage = SendImage(image?.bitmap!!)
                SearchFragmentDirections.actionSearchFragmentToGalleryDetailFragment(
                    sendImage,
                    photo!!
                )
            }
        }
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpSearch(searchViews: MenuItem) {
        searchRes?.queryHint = "Search Image ...                      "
        searchViews.expandActionView()
        searchRes?.maxWidth = Integer.MAX_VALUE
        searchRes?.setIconifiedByDefault(false)
        searchRes?.isIconified = false
        searchRes?.setBackgroundColor(getColor(R.color.white))
        searchViews.actionView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            .setImageResource(R.drawable.ic_baseline_clear_24)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Disposable Is Cleared")
        disposables.clear()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getColor(color: Int) = resources.getColor(color, null)
}