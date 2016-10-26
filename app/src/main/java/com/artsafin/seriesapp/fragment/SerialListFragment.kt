package com.artsafin.seriesapp.fragment

import android.content.Context
import android.database.Cursor
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.dto.Serial

import com.artsafin.seriesapp.data.contract.*

/**
 * A placeholder fragment containing a simple view.
 */
class SerialListFragment : Fragment(), AdapterView.OnItemClickListener {
    private val LOADER_ID = 0

    interface SerialListFragmentHandler {
        fun onSerialClick(serial: Serial)
    }

    private var searchView: SearchView? = null
    private var search: String? = null

    private var eventHandler: SerialListFragmentHandler? = null
    private var adapter: SimpleCursorAdapter? = null
    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            return CursorLoader(
                    activity,
                    Serials.urlSerials(search),
                    Serials.ListProjection.FIELDS,
                    null,
                    null,
                    Serials.ListProjection.SORT_ORDER)
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

        adapter = SimpleCursorAdapter(
                activity,
                android.R.layout.simple_list_item_2,
                null,
                arrayOf<String>(Serials.NAME, Serials.IMAGE),
                intArrayOf(android.R.id.text1, android.R.id.text2),
                0)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SerialListFragmentHandler) {
            eventHandler = context as SerialListFragmentHandler?
        } else {
            throw RuntimeException(context!!.toString() + " must implement " + SerialListFragmentHandler::class.java.simpleName)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        val listView = view!!.findViewById(R.id.serial_list_listview) as ListView
        listView.adapter = adapter
        listView.onItemClickListener = this

        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        val item = menu!!.add(R.string.search)
        item.setIcon(android.R.drawable.ic_menu_search)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        searchView = SearchView(activity)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

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
        })
        searchView?.setOnCloseListener {
            if (!TextUtils.isEmpty(searchView!!.query)) {
                searchView!!.setQuery(null, true)
            }
            true
        }
        searchView?.setIconifiedByDefault(true)
        item.actionView = searchView
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_serial_list, container, false)
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val cursor = parent.getItemAtPosition(position) as Cursor?
        if (cursor != null) {
            eventHandler?.onSerialClick(Serials.ListProjection.toValueObject(cursor))
        }
    }
}
