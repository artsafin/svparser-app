package com.artsafin.seriesapp.adapter

import android.content.Context
import android.database.CrossProcessCursorWrapper
import android.database.Cursor
import android.support.v4.widget.SimpleCursorAdapter
import android.view.View
import android.widget.TextView
import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.data.ListCursor
import com.artsafin.seriesapp.data.contract.Episodes
import com.artsafin.seriesapp.dto.Episode

class EpisodesAdapter(context: Context)
    : SimpleCursorAdapter(context, R.layout.item_episode, null, Episodes.ListProjection.FIELDS, null, 0) {

    private data class ViewTag(val textField: TextView)

    fun refreshItem(ep: Episode, view: TextView) {
        view.text = ep.comment

        if (ep.isWatched) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_view, 0)
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        if (view == null || context == null || cursor == null) {
            return
        }

        if (view.tag == null) {
            view.tag = ViewTag(view.findViewById(R.id.item_episode_text) as TextView)
        }

        val episode = Episodes.ListProjection.toValueObject(cursor)

        refreshItem(episode, (view.tag as ViewTag).textField)
    }
}