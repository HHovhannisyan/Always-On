package com.example.alwayson.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)

data class ErrorSites(
    @Json(name = "code")
    val code: String,
    @Json(name = "clientId")
    val clientId: String,
    @Json(name = "name")
    var name: String,
    @Json(name = "domain")
    val domain: String

)
