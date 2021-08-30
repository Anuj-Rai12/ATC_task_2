package com.example.cargo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.cargo.BuildConfig
import com.example.cargo.R
import com.example.cargo.databinding.CustomProgressBarLayoutBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.math.roundToInt

class MyDialog :
    androidx.fragment.app.DialogFragment() {
    private val args: MyDialogArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alterDialog = AlertDialog.Builder(requireActivity()).setTitle(args.title)
        alterDialog.setMessage(args.message).setPositiveButton("ok") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        return alterDialog.create()
    }
}

class CustomProgress @Inject constructor(private val customProgressBar: CustomProgressBar) {
    fun hideLoading() {
        customProgressBar.dismiss()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun showLoading(context: Context, string: String?, boolean: Boolean = false) {
        val con = context as Activity
        customProgressBar.show(con, string, boolean)
    }
}

class CustomProgressBar @Inject constructor() {
    private var alertDialog: AlertDialog? = null

    @SuppressLint("SourceLockedOrientationActivity")
    fun show(context: Context, title: CharSequence?, flag: Boolean = true) {
        val con = (context as Activity)
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = CustomProgressBarLayoutBinding.inflate(inflater)
        title?.let {
            binding.textView.text = it
        }
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        this.alertDialog = alertDialog.create()
        this.alertDialog?.show()
    }

    fun dismiss() = alertDialog?.dismiss()
}

fun checkFieldValue(string: String) = string.isEmpty() || string.isBlank()

fun manipulateColor(color: Int, factor: Float): Int {
    val a: Int = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(
        a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255)
    )
}

data class Image(
    val bitmap: Bitmap,
    val imageView: ImageView
)

@Parcelize
data class SendImage(
    val bitmap: Bitmap
) : Parcelable

data class PalletColor(
    val rgb: Int,
    val titleTextColor: Int,
    val bodyTextColor: Int,
    val darkThemColor: Int
)

inline fun SearchView.onQueasyListenerChanged(crossinline Listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            Listener(newText.orEmpty())
            return true
        }

    })
}

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("ShowToast")
fun Activity.msg(title: String, response: (() -> Unit)? = null) {
    val snackBar =
        Snackbar.make(findViewById(android.R.id.content), title, Snackbar.LENGTH_LONG)
            .setAction("RETRY") {
                response?.invoke()
            }.setTextColor(resources.getColor(R.color.app_color, null))
            .setActionTextColor(resources.getColor(R.color.red_color, null))
    snackBar.view.setBackgroundColor(resources.getColor(R.color.snackBar_color, null))
    snackBar.show()
}

fun View.hide() {
    this.isVisible = false
}

fun View.show() {
    this.isVisible = true
}

object FileUtils {
    const val Base_Url = "https://api.flickr.com/"
    const val get = "services/rest"
    const val api_key = BuildConfig.API_KEY
    const val per_page = 5
    const val method = "flickr.photos.getRecent"
    const val method2 = "flickr.photos.search"
    const val format = "json"
    const val extras = "url_s"
    const val noJsonCallback = 1
    const val text = "Dog"
    const val timeToSearch = 1000
}