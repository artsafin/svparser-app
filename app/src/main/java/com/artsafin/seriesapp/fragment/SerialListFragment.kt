package com.artsafin.seriesapp.fragment

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.activity.GlobalViewstate
import com.artsafin.seriesapp.activity.Viewstate
import com.artsafin.seriesapp.adapter.SerialListCursorAdapter
import com.artsafin.seriesapp.dto.Serial

import com.artsafin.seriesapp.data.contract.*

/**
 * A placeholder fragment containing a simple view.
 */
open class SerialListFragment : Fragment(), AdapterView.OnItemClickListener {
    open protected val LOADER_ID = 0

    var clickHandler: (serial: Serial) -> Unit = {}

    private var searchView: SearchView? = null
    private var search: String? = null

    lateinit private var adapter: SerialListCursorAdapter

    open protected fun getLoader() = CursorLoader(
            activity,
            Serials.fetchUrl(search),
            Serials.ListProjection.FIELDS,
            null,
            null,
            Serials.ListProjection.SORT_ORDER)

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?) = getLoader()

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
            adapter.swapCursor(data)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            adapter.swapCursor(null)
        }
    }

    private val searchViewCallbacks = object: SearchView.OnQueryTextListener, SearchView.OnCloseListener {
        override fun onClose(): Boolean {
            if (!TextUtils.isEmpty(searchView?.query)) {
                searchView?.setQuery(null, true)
            }
            return true
        }

        override fun onQueryTextSubmit(query: String) = true

        override fun onQueryTextChange(newText: String): Boolean {
            val newFilter = if (!TextUtils.isEmpty(newText)) newText else null

            if (search == null && newFilter == null) {
                return true
            }
            if (search != null && search == newFilter) {
                return true
            }
            search = newFilter
            loaderManager.restartLoader(LOADER_ID, null, loaderCallbacks)
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = SerialListCursorAdapter(activity)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        val listView = view?.findViewById(R.id.serial_list_listview) as ListView
        listView.adapter = adapter
        listView.onItemClickListener = this

        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        searchView = SearchView(activity).apply {
            setOnQueryTextListener(searchViewCallbacks)
            setOnCloseListener(searchViewCallbacks)
            setIconifiedByDefault(true)
        }

        if (menu != null) {
            val item = menu.add(R.string.search)
            item.setIcon(android.R.drawable.ic_menu_search)
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            item.actionView = searchView
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_serial_list, container, false)

    override fun onResume() {
        super.onResume()

        if (GlobalViewstate.serial.isDirty) {
            loaderManager.restartLoader(LOADER_ID, null, loaderCallbacks)
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val cursor = parent.getItemAtPosition(position) as Cursor?

        if (cursor != null) {
            clickHandler(Serials.ListProjection.toValueObject(cursor))
        }
    }
}
