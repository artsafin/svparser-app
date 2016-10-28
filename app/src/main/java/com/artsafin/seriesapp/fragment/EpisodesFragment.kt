package com.artsafin.seriesapp.fragment


import android.app.ProgressDialog
import android.database.Cursor
import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.adapter.EpisodesAdapter

import com.artsafin.seriesapp.data.contract.*
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season

class EpisodesFragment : Fragment(), AdapterView.OnItemClickListener {
    var clickHandler: (ep: Episode) -> Boolean = { true }

    private val TAG = EpisodesFragment::class.java.simpleName
    private val LOADER_ID = 2

    private var season: Season? = null

    private var progressDialog: ProgressDialog? = null
    private var listView: ListView? = null

    lateinit private var adapter: EpisodesAdapter

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            return CursorLoader(
                    activity,
                    Episodes.urlEpisodesBySeason(season?.id ?: -1),
                    Episodes.ListProjection.FIELDS,
                    null,
                    null,
                    null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
            adapter.swapCursor(data)

            progressDialog?.dismiss()
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            adapter.swapCursor(null)

            progressDialog?.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            season = arguments.getSerializable(EXTRA_SEASON) as Season
        }

        adapter = EpisodesAdapter(activity)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        progressDialog = ProgressDialog(activity).apply() {
            isIndeterminate = true
            setMessage(getString(R.string.loading))
            setCancelable(false)
            show()
        }

        return inflater?.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listView = view?.findViewById(R.id.episodes_listview) as ListView
        listView?.adapter = adapter
        listView?.onItemClickListener = this

        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val c = parent.getItemAtPosition(position) as Cursor?

        if (c != null) {
            val ep = Episodes.ListProjection.toValueObject(c)

            if (clickHandler(ep)) {
                val (uri, values) = Watches.episodeWatchedInsert(ep._id)

                Log.d(TAG, "onItemClick: insert before: $uri, values: $values")
                val newUri = activity.contentResolver.insert(uri, values)
                Log.d(TAG, "onItemClick: insert: $newUri")

                ep.isWatched = true
                adapter.refreshItem(view as TextView, ep)
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
