package com.artsafin.seriesapp.dto

import java.io.Serializable

data class Season(val id: Long, var serialId: Long, val name: String, val url: String, val year: String) : Serializable {
    val fullUrl = "http://seasonvar.ru$url"
}
