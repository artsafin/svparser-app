package com.artsafin.seriesapp.activity

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.fragment.EpisodesFragment
import com.artsafin.seriesapp.fragment.SeasonsFragment
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

class SeasonsActivity : BaseActivity(MainActivity::class.java), SeasonsFragment.SeasonsFragmentHandler {
    private val TAG = SeasonsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onSeasonClick(season: Season) {
        Log.d(TAG, "onSeasonClick: url=" + season.fullUrl + " " + season.toString())

        val intent = Intent(this, EpisodesActivity::class.java)
        intent.putExtra(EpisodesFragment.EXTRA_SEASON, season)
        startActivity(intent)
    }
}
