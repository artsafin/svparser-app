package com.artsafin.seriesapp.fragment


import android.app.ProgressDialog
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.*
import android.widget.AdapterView

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.adapter.EpisodesAdapter

import com.artsafin.seriesapp.data.contract.*
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season
import android.support.v7.view.ActionMode
import android.util.Log
import com.artsafin.seriesapp.activity.GlobalViewstate
import com.artsafin.seriesapp.data.SeriesProvider
import com.artsafin.seriesapp.util.*

class EpisodesFragment: Fragment(), AdapterView.OnItemClickListener {

    var clickHandler: (ep: Episode) -> Boolean = { true }

    private val TAG = EpisodesFragment::class.java.simpleName
    private val LOADER_ID = 2

    lateinit private var season: Season

    private var listView: MultiChoiceListView? = null

    lateinit private var adapter: EpisodesAdapter

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            Log.d(TAG, "onCreateLoader: season=$season")

            val fetchCached = args?.getBoolean(SeriesProvider.PARAM_CACHED) ?: false
            val url = Episodes.BySeason.fetchBySeasonUrl(season.id, fetchCached)

            return CursorLoader(
                    activity,
                    url,
                    Episodes.ListProjection.FIELDS,
                    null,
                    null,
                    null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
            Log.d(TAG, "onLoadFinished: count=${data.count}")
            adapter.swapCursor(data)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            Log.d(TAG, "onLoaderReset")
            adapter.swapCursor(null)
        }
    }

    private val menuCallbacks = object : ActionMode.Callback {
        override fun onDestroyActionMode(mode: ActionMode?) { }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.context_episode, menu)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = when (item?.itemId) {
            R.id.menu_context_episode_toggle -> toggleChecked()
            R.id.menu_context_episode_watch_above -> watchAboveFirstChecked()
            R.id.menu_context_episode_unwatch_below -> unwatchBelowLastChecked()
            R.id.menu_context_episode_select_all -> {
                listView?.forEach { listView?.setItemChecked(it, true) }
                false
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            season = arguments.getSerializable(EXTRA_SEASON) as Season
        } else {
            throw RuntimeException("Argument EXTRA_SEASON must be passed to ${javaClass.simpleName}")
        }

        adapter = EpisodesAdapter(activity)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listView = (view?.findViewById(R.id.episodes_listview) as MultiChoiceListView).apply {
            emptyView = activity.findViewById(R.id.activity_progress)
            adapter = this@EpisodesFragment.adapter
            enableMultiChoice(menuCallbacks)
            onItemClickListener = this@EpisodesFragment
        }

        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks)
    }

    override fun onResume() {
        super.onResume()

        GlobalViewstate.episode.ifDirty(LOADER_ID) {
            restartLoaderCached()
        }
    }

    private fun toggleChecked(): Boolean {
        var firstWatched: Boolean? = null

        val map = mutableSetOf<Episode>()

        listView?.let { listView ->
            listView.forEachChecked {
                val cur = listView.getItemAtPosition(it) as Cursor
                val ep = Episodes.ListProjection.toValueObject(cur)

                if (firstWatched == null) {
                    firstWatched = ep.isWatched
                }
                map.add(ep)
            }
        }

        markEpisodesWatched(!(firstWatched ?: false), map)

        // toggleChecked always returns false not to finish the ActionMode
        return false
    }

    private fun watchAboveFirstChecked(): Boolean {
        val firstCheckedPos = listView?.getFirstCheckedPosition() ?: -1
        Log.d(TAG, "watchAboveFirstChecked: $firstCheckedPos")

        // Nothing to do if no selection or the first is checked
        if (firstCheckedPos < 1) {
            return false
        }

        markWatchedRange(true, 0..firstCheckedPos-1)

        return true
    }

    private fun unwatchBelowLastChecked(): Boolean {
        val lastCheckedPos = listView?.getLastCheckedPosition() ?: -1
        val lastListPos = (listView?.count ?: 0) - 1
        if (lastListPos < 0 || lastCheckedPos < 0 || lastCheckedPos >= lastListPos) {
            return false
        }

        markWatchedRange(false, lastCheckedPos..lastListPos)

        return true
    }

    private fun markEpisodesWatched(watched: Boolean, items: Set<Episode>) {
        if (items.size == 0) {
            return
        }

        items.forEach { it.isWatched = watched }

        if (watched) {
            val (uri, values) = Episodes.Cached.insertManyQuery(items)
            activity.contentResolver.bulkInsert(uri, values)
        } else {
            val (uri, where, whereArgs) = Episodes.Cached.deleteManyQuery(items)
            activity.contentResolver.delete(uri, where, whereArgs)
        }

        GlobalViewstate.episode.dirty()
        // Refresh from cached cursor
        restartLoaderCached()
    }

    private fun restartLoaderCached() {
        val args = Bundle().apply { putBoolean(SeriesProvider.PARAM_CACHED, true) }
        loaderManager.restartLoader(LOADER_ID, args, loaderCallbacks)
    }

    private fun markWatchedRange(watched: Boolean, range: IntRange) {
        val map = mutableSetOf<Episode>()

        Log.d(TAG, "markWatchedRange: $range = $watched")

        listView?.let { listView ->
            for (i in range) {
                val cur = listView.getItemAtPosition(i) as Cursor
                val ep = Episodes.ListProjection.toValueObject(cur)
                map.add(ep)
            }
        }

        markEpisodesWatched(watched, map)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val c = parent.getItemAtPosition(position) as Cursor?

        if (c != null) {
            val ep = Episodes.ListProjection.toValueObject(c)

            if (clickHandler(ep)) {
                if (!ep.isWatched) {
                    markEpisodesWatched(true, setOf(ep))
                }
            }
        }
    }

    companion object {

        val EXTRA_SEASON = "season"

        fun newInstance(season: Season): EpisodesFragment {
            val fragment = EpisodesFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_SEASON, season)
            fragment.arguments = args
            return fragment
        }
    }
}