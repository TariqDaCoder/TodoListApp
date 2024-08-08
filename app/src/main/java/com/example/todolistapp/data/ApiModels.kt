package com.example.todolistapp

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Todo(
    @Json(name = "id") val id: String,
    @Json(name = "text") val text: String,
    @Json(name = "completed") val completed: Boolean
)

@JsonClass(generateAdapter = true)
data class UserRegistration(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class UserLogin(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "token") val token: String,
    @Json(name = "id") val id: String
)

// You might also want to create a class for the API key
data class ApiKey(val value: String)

// If the API returns errors in a specific format, you might want to create an Error class
@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "message") val message: String
)