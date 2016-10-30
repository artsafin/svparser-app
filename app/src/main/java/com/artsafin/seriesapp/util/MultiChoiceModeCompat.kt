package com.artsafin.seriesapp.util

import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.artsafin.seriesapp.R

class MultiChoiceModeCompat(activity: AppCompatActivity, listView: ListView, userMenuCallbacks: ActionMode.Callback) {
    private val TAG = MultiChoiceModeCompat::class.java.simpleName

    private var actionMode: ActionMode? = null
    var userLongClickListener: AdapterView.OnItemLongClickListener? = null

    init {
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener(
                fun(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
//                    Log.d(TAG, "onItemLongClickListener: $position")

                    val userClickResult = userLongClickListener?.onItemLongClick(parent, view, position, id) ?: false
//                    Log.d(TAG, "onItemLongClickListener: user: $userClickResult")
                    if (userClickResult) {
                        return userClickResult
                    }

                    listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                    listView.setItemChecked(position, true)
                    if (actionMode == null) {
                        activity.startSupportActionMode(actionModeCallbacks)
//                        Log.d(TAG, "onItemLongClickListener: start action mode")
                    }
                    actionMode?.invalidate()
                    return true
                })
    }

    fun isActive() = actionMode != null
    fun invalidate() = actionMode?.invalidate()

    private val actionModeCallbacks = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val userResult = userMenuCallbacks.onCreateActionMode(mode, menu)

//            Log.d(TAG, "onCreateActionMode: user: $userResult")

            actionMode = if (userResult) mode else null

            return userResult
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
//            Log.d(TAG, "onPrepareActionMode")
            userMenuCallbacks.onPrepareActionMode(mode, menu)

            if (actionMode != null) {
                if (listView.checkedItemCount == 0) {
//                    Log.d(TAG, "onPrepareActionMode: finish")
                    mode?.finish()
                } else if (listView.checkedItemCount > 1) {
                    mode?.title = activity.resources.getString(R.string.items_selected, listView.checkedItemCount)
                } else {
                    mode?.title = ""
                }
//                Log.d(TAG, "onPrepareActionMode: handled with item count ${listView.checkedItemCount}")
                return true
            }

            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            val userResult = userMenuCallbacks.onActionItemClicked(mode, item)

//            Log.d(TAG, "onActionItemClicked: user $userResult")

            if (userResult) {
//                Log.d(TAG, "onActionItemClicked: finish")
                mode?.finish()
            }

            return userResult
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            userMenuCallbacks.onDestroyActionMode(mode)

//            Log.d(TAG, "onDestroyActionMode")

            actionMode = null
            listView.forEachChecked {
                listView.setItemChecked(it, false)
            }
        }
    }
}