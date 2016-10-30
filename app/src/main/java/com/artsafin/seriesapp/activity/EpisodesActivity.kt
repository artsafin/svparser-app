package com.artsafin.seriesapp.activity

import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.view.ActionMode
import android.util.Log
import android.webkit.MimeTypeMap

import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.fragment.EpisodesFragment
import com.artsafin.seriesapp.R

class EpisodesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            val season = viewState.season ?: throw RuntimeException("Season must be passed in intent to ${javaClass.simpleName}")
            val fragment = EpisodesFragment.newInstance(season)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_content, fragment)
                    .commit()
            title = season.name
        }
    }

    private fun createViewIntent(item: Episode): Intent {
        val uri = Uri.parse(item.file)
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, mimeType)

        return intent
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        (fragment as? EpisodesFragment)?.clickHandler = { ep ->
            val intent = createViewIntent(ep)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
                true
            } else {
                Snackbar.make(findViewById(R.id.activity_content), R.string.no_video_activity, Snackbar.LENGTH_LONG)
                false
            }
        }
    }
}
