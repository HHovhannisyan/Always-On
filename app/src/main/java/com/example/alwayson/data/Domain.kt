package com.example.alwayson.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Domain(
    @Json(name="PartnerId")
    var PartnerId:Int
)

