package com.artsafin.seriesapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.util.LruCache

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

object VolleyLoader {
    private var context: Context? = null
    lateinit private var requestQueue: RequestQueue
    lateinit private var imageLoader: ImageLoader

    @Synchronized fun getImageLoader(ctx: Context): ImageLoader {
        if (context == null) {
            context = ctx
            requestQueue = Volley.newRequestQueue(ctx.applicationContext)
            imageLoader = ImageLoader(requestQueue,
                                      object : ImageLoader.ImageCache {
                                          private val cache = LruCache<String, Bitmap>(20)

                                          override fun getBitmap(url: String): Bitmap? {
                                              return cache.get(url)
                                          }

                                          override fun putBitmap(url: String, bitmap: Bitmap) {
                                              cache.put(url, bitmap)
                                          }
                                      })
        }

        return imageLoader
    }
}
