package com.example.cargo.paginate.girdadaptor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cargo.R
import com.example.cargo.data.Photo
import com.example.cargo.databinding.PhotoItemSecondBinding
import com.example.cargo.utils.Image

class OtherViewHolder(
    private val binding: PhotoItemSecondBinding,
    private val context: Context
) :
    RecyclerView.ViewHolder(binding.root) {
    private var bitmap: Bitmap? = null
    fun bindIt(photo: Photo, image: (Image) -> Unit) {
        val botAnimation = AnimationUtils.loadAnimation(context, R.anim.bot_animation)
        binding.apply {
            galImage.setOnClickListener {
                bitmap?.let { img ->
                    image(Image(img, galImage))
                }
            }
                val params = galImage.layoutParams
                params.height = ((photo.heightS) + (photo.heightS))
                params.width = (((photo.widthS) + (photo.widthS)) + ((photo.widthS) / 4))
                galImage.layoutParams = params
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
                            bitmap = resource
                            galImage.setImageBitmap(resource)
                            galImage.startAnimation(botAnimation)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            placeholder?.let {
                                bitmap = (it as BitmapDrawable).bitmap
                                galImage.setImageBitmap(bitmap)
                                galImage.startAnimation(botAnimation)
                            }
                        }
                    })
            }
        }
    }