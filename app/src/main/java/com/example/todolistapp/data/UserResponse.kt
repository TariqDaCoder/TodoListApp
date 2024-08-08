package com.example.todolistapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "token") val token: String,
    @Json(name = "id") val id: String
)