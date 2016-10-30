package com.artsafin.seriesapp.fragment

import android.net.Uri
import android.support.v4.content.CursorLoader
import com.artsafin.seriesapp.data.contract.Serials

class FavoritesFragment : SerialListFragment() {
    private val TAG = SeasonsFragment::class.java.simpleName
    override val LOADER_ID = 3

    override fun getLoader() = CursorLoader(
            activity,
            Serials.fetchUrl(null),
            Serials.ListProjection.FIELDS,
            Serials.Fav.where,
            Serials.Fav.whereArgs(favorite = true),
            Serials.ListProjection.SORT_ORDER)

    companion object {
        fun newInstance(): FavoritesFragment {
            val fragment = FavoritesFragment()
            return fragment
        }
    }
}
