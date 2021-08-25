package com.example.cargo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import com.example.cargo.databinding.CustomProgressBarLayoutBinding
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

object FileUtils {
    const val Base_Url = "https://api.flickr.com/services/"
    const val get = "rest"
    const val api_key = "6f102c62f41998d151e5a1b48713cf13"
    const val per_page = 20
    const val method = "flickr.photos.getRecent"
    const val format = "json"
    const val extras = "url_s"
    const val noJsonCallback = 1
}