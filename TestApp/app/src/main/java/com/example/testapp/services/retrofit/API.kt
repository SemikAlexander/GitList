package com.example.testapp.services.retrofit

import com.example.testapp.services.model.response.user.UserData
import com.example.testapp.services.model.response.users.UserList
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface API {
    @GET("users")
    suspend fun getAllUsers(
        @Query("since") since: Int,
        @Query("per_page") per_page: Int = 30
    ): UserList

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") userName: String
    ): UserData

    companion object {
        val api by lazy { retrofit.create<API>() }
    }
}