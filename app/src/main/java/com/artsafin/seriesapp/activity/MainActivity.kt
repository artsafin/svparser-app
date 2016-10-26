package com.artsafin.seriesapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.artsafin.seriesapp.fragment.SeasonsFragment
import com.artsafin.seriesapp.fragment.SerialListFragment
import com.artsafin.seriesapp.dto.Serial

class MainActivity : BaseActivity(null), SerialListFragment.SerialListFragmentHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateSerials()
    }

    override fun onSerialClick(serial: Serial) {
        Log.i("MainActivity", serial.toString())

        val intent = Intent(this, SeasonsActivity::class.java)
        intent.putExtra(SeasonsFragment.EXTRA_SERIAL, serial)
        startActivity(intent)
    }
}
