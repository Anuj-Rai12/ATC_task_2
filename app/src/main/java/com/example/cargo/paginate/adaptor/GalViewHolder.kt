package com.example.cargo.paginate.adaptor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cargo.R
import com.example.cargo.data.Photo
import com.example.cargo.databinding.PhotoItemBinding
import com.example.cargo.utils.checkFieldValue
import android.graphics.drawable.BitmapDrawable
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.palette.graphics.Palette
import com.example.cargo.utils.Image
import com.example.cargo.utils.PalletColor
import com.example.cargo.utils.manipulateColor


class GalViewHolder(private val binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private var bitmap: Bitmap? = null
    fun bindIt(
        photo: Photo,
        context: Context,
        color: (PalletColor) -> Unit,
        image: (Image) -> Unit,
        position: Int
    ) {
        binding.apply {
            val botAnimation = AnimationUtils.loadAnimation(context, R.anim.bot_animation)
            imageDescription.apply {
                text = if (!checkFieldValue(photo.title))
                    "@ ${photo.title}"
                else
                    "@ Beautiful"
                startAnimation(botAnimation)
            }
            galImage.startAnimation(botAnimation)
            galImage.setOnClickListener {
                bitmap?.let { bitmap ->
                    image(Image(bitmap = bitmap, galImage))
                }
            }
            val params = galImage.layoutParams
            params.height = ((photo.heightS) + (photo.heightS))
            params.width = ((photo.widthS) + (photo.widthS))
            galImage.layoutParams = params
            galImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                horizontalBias = if (isEven(position))
                    1.0.toFloat()
                else
                    0.0.toFloat()
            }
            Glide.with(context)
                .asBitmap()
                .placeholder(R.drawable.default_img)
                .error(R.drawable.no_image_found)
                .load(photo.urlS)
                .into(object :
                    CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        galImage.setImageBitmap(resource)
                        createPaletteAsync(resource, color)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        placeholder?.let {
                            val anImage = (it as BitmapDrawable).bitmap
                            createPaletteAsync(anImage, color)
                        }
                    }
                })
        }
    }

    private fun isEven(position: Int) = position % 2 == 0
    private fun createPaletteAsync(bitmap: Bitmap, color: (PalletColor) -> Unit) {
        this.bitmap = bitmap
        Palette.from(bitmap).generate { palette ->
            palette?.vibrantSwatch?.let { swatch ->
                val rbg = swatch.rgb
                val darkTheme = manipulateColor(rbg, 0.8.toFloat())
                binding.root.setBackgroundColor(rbg)
                binding.imageDescription.setTextColor(swatch.titleTextColor)
                PalletColor(
                    rgb = rbg,
                    titleTextColor = swatch.titleTextColor,
                    bodyTextColor = swatch.bodyTextColor,
                    darkThemColor = darkTheme
                ).also {
                    color(it)
                }
            }
        }
    }
}