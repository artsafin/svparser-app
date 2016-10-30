package com.artsafin.seriesapp.dto

import java.io.Serializable

data class Serial(val id: Long, val name: String, val image: String, val flags: Serial.Flags = Serial.Flags()) : Serializable {
    data class Flags(var favorite: Boolean = false, var hidden: Boolean = false): Serializable

    var favorite: Boolean
        get() = flags.favorite
        set(value) { flags.favorite = value }
}
