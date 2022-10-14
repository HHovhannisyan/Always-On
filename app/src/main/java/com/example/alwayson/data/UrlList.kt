package com.example.alwayson.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UrlList(
    @Json(name = "Name")
    var Name: String,
    @Json(name = "domains")
    val domains: List<String>
)


