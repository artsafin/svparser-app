package com.artsafin.seriesapp.adapter

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.SimpleCursorAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.toolbox.NetworkImageView
import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.data.contract.Serials

class SerialListCursorAdapter(context: Context)
: SimpleCursorAdapter(context, R.layout.item_serial,
                      null, Serials.ListProjection.FIELDS, null, 0) {

    private data class ViewTag(val image: NetworkImageView, val text: TextView)

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return super.newView(context, cursor, parent)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        if (cursor == null || view == null || context == null) {
            return
        }

        if (view.tag == null) {
            val imageView = view.findViewById(R.id.item_serial_list_image) as NetworkImageView
            val textView = view.findViewById(R.id.item_serial_list_text) as TextView
            view.tag = ViewTag(imageView, textView)
        }

        val serial = Serials.ListProjection.toValueObject(cursor)
        with (view.tag as ViewTag) {
            image.setImageUrl(serial.image, VolleyLoader.getImageLoader(context))
            text.text = serial.name
        }
    }
}