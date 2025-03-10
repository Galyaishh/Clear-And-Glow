package com.example.clear_and_glow.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.clear_and_glow.R
import java.lang.ref.WeakReference

class ImageLoader private constructor(context: Context) {
    private val contextRef = WeakReference(context.applicationContext) // Use app context to avoid memory leaks

    fun loadImage(
        source: Any,
        imageView: ImageView,
        placeholder: Int = R.drawable.unavailable_photo
    ) {
        contextRef.get()?.let { context ->
            Glide.with(context)
                .load(source)
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                        .error(R.drawable.unavailable_photo) // Fallback if loading fails
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Cache images
                )
                .into(imageView)
        }
    }

    companion object {
        @Volatile
        private var instance: ImageLoader? = null

        fun init(context: Context): ImageLoader {
            return instance ?: synchronized(this) {
                instance ?: ImageLoader(context).also { instance = it }
            }
        }

        fun getInstance(): ImageLoader {
            return instance ?: throw IllegalStateException(
                "ImageLoader must be initialized by calling init(context) before use."
            )
        }
    }
}
