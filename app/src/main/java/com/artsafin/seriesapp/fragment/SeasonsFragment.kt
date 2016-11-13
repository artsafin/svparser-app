package com.artsafin.seriesapp.fragment

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.activity.GlobalViewstate
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

import com.artsafin.seriesapp.data.contract.*


class SeasonsFragment: Fragment(), AdapterView.OnItemClickListener {
    var clickHandler: (season: Season) -> Unit = {}

    private val TAG = SeasonsFragment::class.java.simpleName
    private val LOADER_ID = 1

    private var serial: Serial? = null
    lateinit private var adapter: SimpleCursorAdapter

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            return CursorLoader(
                    activity,
                    Seasons.BySerial.urlSeasonsBySerial(serial?.id ?: -1, GlobalViewstate.SeasonsLoaderFlags.noCache),
                    Seasons.ListProjection.FIELDS,
                    null,
                    null,
                    Seasons.ListProjection.SORT_ORDER)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            if (data != null) {
                adapter.swapCursor(data)
            }

            GlobalViewstate.SeasonsLoaderFlags.noCache = false
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            adapter.swapCursor(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            serial = arguments.getSerializable(EXTRA_SERIAL) as Serial
        } else {
            throw RuntimeException("Argument EXTRA_SERIAL must be passed to ${javaClass.simpleName}")
        }

        adapter = SimpleCursorAdapter(
                activity,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf(Seasons.NAME),
                intArrayOf(android.R.id.text1),
                0)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_seasons, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val listview = view?.findViewById(R.id.seasons_listview) as ListView
        listview.adapter = adapter
        listview.onItemClickListener = this
        listview.emptyView = activity.findViewById(R.id.activity_progress)

        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val cursor = parent.getItemAtPosition(position) as Cursor?
        if (cursor != null) {
            clickHandler(Seasons.ListProjection.toValueObject(cursor))
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalViewstate.season.ifDirty(LOADER_ID) {
            loaderManager.restartLoader(LOADER_ID, null, loaderCallbacks)
        }
    }

    companion object {
        val EXTRA_SERIAL = "serial"

        fun newInstance(serial: Serial): SeasonsFragment {
            val fragment = SeasonsFragment()
            fragment.arguments = Bundle().apply { putSerializable(EXTRA_SERIAL, serial) }
            return fragment
        }
    }
}
