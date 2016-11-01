package com.artsafin.seriesapp.dto

import java.io.Serializable

data class Serial(val id: Long, val name: String, val image: String, var favorite: Boolean = false) : Serializable
