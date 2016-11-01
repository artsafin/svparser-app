package com.artsafin.seriesapp.adapter

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.SimpleCursorAdapter
import android.view.View
import android.widget.TextView
import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.data.contract.Episodes
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season

class EpisodeAndSeasonAdapter(context: Context)
    : SimpleCursorAdapter(context, R.layout.item_episode_season, null, Episodes.JoinedSeasonProjection.FIELDS_ALIASES, null, 0) {

    private data class ViewTag(val text1: TextView, val text2: TextView, val text3: TextView)

    private fun refreshItem(epAndSeason: Pair<Episode, Season>, views: ViewTag) {
        val (ep, season) = epAndSeason
        views.text1.text = ep.comment
        views.text2.text = season.name
        views.text3.text = ep.updateTs
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        if (view == null || context == null || cursor == null) {
            return
        }

        if (view.tag == null) {
            view.tag = ViewTag(view.findViewById(R.id.item_episode_season_text1) as TextView,
                               view.findViewById(R.id.item_episode_season_text2) as TextView,
                               view.findViewById(R.id.item_episode_season_text3) as TextView)
        }

        val epAndSeason = Episodes.JoinedSeasonProjection.toValueObject(cursor)

        refreshItem(epAndSeason, view.tag as ViewTag)
    }
}