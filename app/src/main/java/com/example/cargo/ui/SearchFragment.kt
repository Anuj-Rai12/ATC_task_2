package com.example.cargo.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.example.cargo.MainActivity
import com.example.cargo.R
import com.example.cargo.TAG
import com.example.cargo.databinding.FragmentSearchBinding
import com.example.cargo.utils.onQueasyListenerChanged
import com.example.cargo.viewmodel.MaiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private var searchRes: SearchView? = null
    private var searchQuery: String? = null
    private val viewModel: MaiViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        toolBarSearch()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toolBarSearch() {
        MainActivity.toolbar?.let { toolbar ->
            toolbar.menu?.clear()
            toolbar.inflateMenu(R.menu.scr_menu)
            val searchViews = toolbar.menu.findItem(R.id.my_search_view)
            searchRes = searchViews?.actionView as SearchView
            setUpSearch(searchViews)
            searchRes?.onQueasyListenerChanged { str ->
                searchQuery = str
                Log.i(TAG, "SearchQueryChange: $str")
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getColor(color: Int) = resources.getColor(color, null)
}