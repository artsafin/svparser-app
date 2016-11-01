package com.artsafin.seriesapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.Menu

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.fragment.EpisodesFragment
import com.artsafin.seriesapp.fragment.SeasonsFragment
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

class SeasonsActivity : BaseActivity() {
    private val TAG = SeasonsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            val serial = viewState.serial ?: throw RuntimeException("Serial must be passed in intent to ${javaClass.simpleName}")
            val fragment = SeasonsFragment.newInstance(serial)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_content, fragment)
                    .commit()

            title = serial.name
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (viewState.serial != null) {
            menuInflater.inflate(R.menu.serial, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()

        invalidateOptionsMenu()
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        (fragment as? SeasonsFragment)?.clickHandler = { season ->
            Log.d(TAG, "onSeasonClick: url=" + season.fullUrl + " " + season.toString())

            startActivity(Intent(this, EpisodesActivity::class.java)
                                  .with(viewState.append(season)))
        }
    }
}
