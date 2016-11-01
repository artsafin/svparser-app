package com.artsafin.seriesapp.util

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ListView

class MultiChoiceListView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : ListView(context, attrs, defStyleAttr, defStyleRes) {
    private val TAG = MultiChoiceListView::class.java.simpleName

    private var itemClickListener: OnItemClickListener? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, android.R.attr.listViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    var actionMode: MultiChoiceModeCompat? = null

    fun enableMultiChoice(menuCallbacks: ActionMode.Callback?) {
        if (menuCallbacks != null) {
            actionMode = MultiChoiceModeCompat(context as AppCompatActivity, this, menuCallbacks)
        } else {
            actionMode = null
        }
    }

    override fun setOnItemClickListener(listener: OnItemClickListener?) {
        super.setOnItemClickListener(null)

        itemClickListener = listener
    }

    override fun performItemClick(view: View?, position: Int, id: Long): Boolean {
//        Log.d(TAG, "performItemClick: $position, id=$id")

        val isActionModeActive = actionMode?.isActive() ?: false

        if (!isActionModeActive) {
//            Log.d(TAG, "performItemClick: passed to user")
            choiceMode = AbsListView.CHOICE_MODE_NONE
            val userClickRes = super.performItemClick(view, position, id)

            if (itemClickListener == null) {
                return false
            }

            itemClickListener?.onItemClick(this, view, position, id)
            return userClickRes
        } else {
            choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
            val res = super.performItemClick(view, position, id)
            actionMode?.invalidate()

            return res
        }
    }
}