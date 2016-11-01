package com.artsafin.seriesapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.data.contract.Seasons
import com.artsafin.seriesapp.fragment.EpisodesFragment
import com.artsafin.seriesapp.fragment.FavoritesFragment
import com.artsafin.seriesapp.fragment.RecentlyWatchedFragment
import com.artsafin.seriesapp.fragment.SerialListFragment

class RecentlyWatchedActivity : BaseActivity() {
    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val fragment = RecentlyWatchedFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_content, fragment)
                    .commit()

            title = getString(R.string.recently_watched)
        }
    }
    override fun onResume() {
        super.onResume()

        navigationView.setCheckedItem(R.id.nav_recently_watched)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        (fragment as? RecentlyWatchedFragment)?.clickHandler = { epAndSeason ->
            val (episode, season) = epAndSeason
            LOGD("clickHandler: episode=$episode season=$season")

            val intent = Intent(this, EpisodesActivity::class.java)
                    .with(viewState.append(season))

            startActivity(intent)
        }
    }
}
