package com.example.clear_and_glow.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.clear_and_glow.R
import com.google.firebase.storage.FirebaseStorage
import java.lang.ref.WeakReference


class ImageLoader private constructor(context: Context) {
    private val contextRef = WeakReference(context)

    fun loadImage(
        source: String?,
        imageView: ImageView,
        placeholder: Int = R.drawable.ic_products
    ) {
        contextRef.get()?.let { context ->
            Glide.with(imageView.context).clear(imageView)

            if (!source.isNullOrEmpty()) {
                if (source.startsWith("gs://")) {
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(source)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        loadImageFromUrl(uri, imageView, placeholder)
                    }.addOnFailureListener {
                        imageView.setImageResource(placeholder)
                    }
                } else {
                    loadImageFromUrl(Uri.parse(source), imageView, placeholder)
                }
            } else {
                imageView.setImageResource(placeholder) // Use placeholder if source is null or empty
            }
        }
    }

    private fun loadImageFromUrl(uri: Uri, imageView: ImageView, placeholder: Int) {
        Glide.with(imageView.context)
            .load(uri)
            .centerCrop()
            .placeholder(placeholder).
            diskCacheStrategy(DiskCacheStrategy.NONE).
            into(imageView)
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