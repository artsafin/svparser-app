package com.artsafin.seriesapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.util.Log

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

        if (savedInstanceState == null && intent != null) {
            val fragment = SeasonsFragment.newInstance(intent.extras)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_content, fragment)
                    .commit()

            val serial = intent.getSerializableExtra(SeasonsFragment.EXTRA_SERIAL) as Serial?
            if (serial != null) {
                title = serial.name
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        (fragment as? SeasonsFragment)?.clickHandler = { season ->
            Log.d(TAG, "onSeasonClick: url=" + season.fullUrl + " " + season.toString())

            val intent = Intent(this, EpisodesActivity::class.java)
            intent.putExtra(EpisodesFragment.EXTRA_SEASON, season)
            startActivity(intent)
        }
    }
}
