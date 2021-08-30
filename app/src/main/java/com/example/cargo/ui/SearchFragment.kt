package com.example.cargo.ui

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
import androidx.navigation.fragment.findNavController
import com.example.cargo.MainActivity
import com.example.cargo.R
import com.example.cargo.TAG
import com.example.cargo.databinding.FragmentSearchBinding
import com.example.cargo.utils.*
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private var searchRes: SearchView? = null
    private val viewModel: MaiViewModel by viewModels()
    private val disposables = CompositeDisposable()

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
            if (it.isNotBlank() && it.isNotEmpty() && !it.isNullOrBlank() && networkUtils.isConnected()) {
                MainActivity.toolbar?.title = it
                searchRes?.setQuery(it, false)
                dir(title = it, choose = 23)
            } else if (it.isNotBlank() && it.isNotEmpty() && !it.isNullOrBlank() && !networkUtils.isConnected()) {
                requireActivity().msg("No Internet Connection :( ") {
                    if (networkUtils.isConnected())
                        dir(title = it, choose = 23)
                }
            }
        }
    }


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
            val searchViews = toolbar.menu.findItem(R.id.my_search_view)
            searchRes = searchViews?.actionView as SearchView
            setUpSearch(searchViews)
        }
    }

    private fun dir(
        choose: Int = 0,
        title: String = "Error",
        message: String = "UnWanted Error"
    ) {
        val action = when (choose) {
            0 -> SearchFragmentDirections.actionGlobalMyDialog(title, message)
            else -> {
                viewModel.searchQuery.value = ""
                SearchFragmentDirections.actionSearchFragmentToShowResultFragment(title)
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
        activity?.window?.statusBarColor = getColor(R.color.app_color)
        MainActivity.toolbar?.let {
            it.setTitleTextColor(getColor(R.color.white))
            it.setBackgroundColor(getColor(R.color.app_color))
        }
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