package com.artsafin.seriesapp.fragment


import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.artsafin.seriesapp.R

import com.artsafin.seriesapp.data.contract.*
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season

class EpisodesFragment : Fragment(), AdapterView.OnItemClickListener {
    var clickHandler: (ep: Episode) -> Unit = {}

    private val TAG = EpisodesFragment::class.java.simpleName
    private val LOADER_ID = 2


    private var season: Season? = null

    private var progressDialog: ProgressDialog? = null
    private var listView: ListView? = null

    lateinit private var adapter: SimpleCursorAdapter

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

        adapter = SimpleCursorAdapter(
                activity,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf(Episodes.COMMENT),
                intArrayOf(android.R.id.text1),
                0)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        progressDialog = ProgressDialog(activity)
        progressDialog?.isIndeterminate = true
        progressDialog?.setMessage(getString(R.string.loading))
        progressDialog?.setCancelable(false)
        progressDialog?.show()

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
            clickHandler(Episodes.ListProjection.toValueObject(c))
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
