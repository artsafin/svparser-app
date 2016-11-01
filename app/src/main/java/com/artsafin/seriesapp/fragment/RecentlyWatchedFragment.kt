package com.artsafin.seriesapp.fragment


import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.AdapterView

import com.artsafin.seriesapp.R

import com.artsafin.seriesapp.data.contract.*
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season
import android.util.Log
import com.artsafin.seriesapp.activity.GlobalViewstate
import com.artsafin.seriesapp.adapter.EpisodeAndSeasonAdapter
import com.artsafin.seriesapp.util.*

class RecentlyWatchedFragment : Fragment(), AdapterView.OnItemClickListener {

    var clickHandler: (epAndSeason: Pair<Episode, Season>) -> Unit = { }

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    private val LOADER_ID = 4

    private var listView: MultiChoiceListView? = null

    lateinit private var adapter: SimpleCursorAdapter

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            val (url, selArgs) = Episodes.Cached.fetchRecentlyWatchedUrl(Episodes.JoinedSeasonProjection.FIELDS)

            return CursorLoader(
                    activity,
                    url,
                    selArgs.projection,
                    selArgs.selection,
                    selArgs.selectionArgs,
                    selArgs.sortOrder)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
            LOGD("onLoadFinished: count=${data.count}")
            adapter.swapCursor(data)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            adapter.swapCursor(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = EpisodeAndSeasonAdapter(activity)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listView = (view?.findViewById(R.id.episodes_listview) as MultiChoiceListView).apply {
//            emptyView = activity.findViewById(R.id.activity_progress)
            adapter = this@RecentlyWatchedFragment.adapter
            onItemClickListener = this@RecentlyWatchedFragment
        }

        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks)
    }

    override fun onResume() {
        super.onResume()

        GlobalViewstate.episode.ifDirty(LOADER_ID) {
            loaderManager.restartLoader(LOADER_ID, null, loaderCallbacks)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.recent, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_recent_clear -> {
            AlertDialog.Builder(activity)
                    .setMessage(R.string.are_you_sure_to_clear_watches)
                    .setPositiveButton(R.string.yes, { dialog, which ->
                        dialog.dismiss()

                        val (url, where, args) = Episodes.Cached.deleteAllQuery()
                        activity.contentResolver.delete(url, where, args)
                        loaderManager.restartLoader(LOADER_ID, null, loaderCallbacks)
                        GlobalViewstate.episode.dirty()
                    })
                    .setNegativeButton(R.string.no, { dialog, which -> dialog.dismiss() })
            .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val c = parent.getItemAtPosition(position) as Cursor?

        if (c != null) {
            val data = Episodes.JoinedSeasonProjection.toValueObject(c)

            clickHandler(data)
        }
    }

    companion object {
        fun newInstance() = RecentlyWatchedFragment()
    }
}