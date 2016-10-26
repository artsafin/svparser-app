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
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

import com.artsafin.seriesapp.data.contract.*


class SeasonsFragment : Fragment(), AdapterView.OnItemClickListener {
    private val TAG = SeasonsFragment::class.java.simpleName
    private val LOADER_ID = 1

    interface SeasonsFragmentHandler {
        fun onSeasonClick(season: Season)
    }

    private var serial: Serial? = null
    private var eventHandler: SeasonsFragmentHandler? = null
    private var adapter: SimpleCursorAdapter? = null
    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            return CursorLoader(
                    activity,
                    Seasons.urlSeasonsBySerial(serial?.id ?: -1),
                    Seasons.ListProjection.FIELDS,
                    null,
                    null,
                    Seasons.ListProjection.SORT_ORDER)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
            adapter!!.swapCursor(data)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            adapter!!.swapCursor(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            serial = arguments.getSerializable(EXTRA_SERIAL) as Serial

            Log.d(TAG, "onCreate: " + if (serial == null) "<null>" else serial!!.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_seasons, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = SimpleCursorAdapter(
                activity,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf<String>(Seasons.NAME),
                intArrayOf(android.R.id.text1),
                0)

        val listview = view!!.findViewById(R.id.seasons_listview) as ListView
        listview.adapter = adapter
        listview.onItemClickListener = this

        val loaderArgs = Bundle()
        loaderArgs.putLong(EXTRA_SERIAL, serial!!.id)
        loaderManager.initLoader(LOADER_ID, loaderArgs, loaderCallbacks)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SeasonsFragmentHandler) {
            eventHandler = context as SeasonsFragmentHandler?
        } else {
            throw RuntimeException(context!!.toString() + " must implement " + SeasonsFragmentHandler::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        eventHandler = null
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val cursor = parent.getItemAtPosition(position) as Cursor?
        if (cursor != null) {
            eventHandler?.onSeasonClick(Seasons.ListProjection.toValueObject(cursor))
        }
    }

    companion object {
        val EXTRA_SERIAL = "serial"

        fun newInstance(args: Bundle): SeasonsFragment {
            val fragment = SeasonsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
