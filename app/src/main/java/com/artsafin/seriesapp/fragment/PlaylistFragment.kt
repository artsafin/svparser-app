package com.artsafin.seriesapp.fragment


import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.data.contract.*
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Playlist

class PlaylistFragment : Fragment(), AdapterView.OnItemClickListener {
    private val TAG = PlaylistFragment::class.java.simpleName

    private var playlist: Playlist? = null

    private var eventHandler: EpisodesFragment.EpisodesFragmentHandler? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is EpisodesFragment.EpisodesFragmentHandler) {
            eventHandler = context as EpisodesFragment.EpisodesFragmentHandler?
        } else {
            throw RuntimeException(
                    context!!.toString() + " must implement " + EpisodesFragment.EpisodesFragmentHandler::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            playlist = arguments.getSerializable(EXTRA_PLAYLIST) as Playlist
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = ArrayAdapter(
                activity,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                playlist!!)

        val listView = view!!.findViewById(R.id.episodes_listview) as ListView
        listView.adapter = adapter
        listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val c = parent.getItemAtPosition(position) as Cursor

        val ep = Episodes.ListProjection.toValueObject(c)

        eventHandler!!.onSingleEpisodeClick(ep)
    }

    companion object {
        val EXTRA_PLAYLIST = "playlist"

        fun newInstance(pls: Playlist): PlaylistFragment {
            val fragment = PlaylistFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_PLAYLIST, pls)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
