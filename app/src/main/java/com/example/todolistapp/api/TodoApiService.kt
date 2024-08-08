package com.example.todolistapp.api

import com.example.todolistapp.data.Todo
import com.example.todolistapp.data.UserRegistration
import com.example.todolistapp.data.UserLogin
import com.example.todolistapp.data.UserResponse
import retrofit2.http.*

interface TodoApiService {
    @GET("api/users/{userId}/todos")
    suspend fun getTodos(
        @Path("userId") userId: String,
        @Query("apikey") apiKey: String,
        @Header("Authorization") token: String
    ): List<Todo>

    @POST("api/users/{userId}/todos")
    suspend fun createTodo(
        @Path("userId") userId: String,
        @Query("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body todo: Todo
    ): Todo

    @PUT("api/users/{userId}/todos/{id}")
    suspend fun updateTodo(
        @Path("userId") userId: String,
        @Path("id") todoId: String,
        @Query("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body todo: Todo
    ): Todo

    @DELETE("api/users/{userId}/todos/{id}")
    suspend fun deleteTodo(
        @Path("userId") userId: String,
        @Path("id") todoId: String,
        @Query("apikey") apiKey: String,
        @Header("Authorization") token: String
    )

    @POST("api/users/register")
    suspend fun registerUser(
        @Query("apikey") apiKey: String,
        @Body user: UserRegistration
    ): UserResponse

    @POST("api/users/login")
    suspend fun loginUser(
        @Query("apikey") apiKey: String,
        @Body user: UserLogin
    ): UserResponse
}