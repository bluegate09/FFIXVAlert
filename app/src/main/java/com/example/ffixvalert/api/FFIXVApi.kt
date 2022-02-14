package com.example.ffixvalert.api

import com.example.ffixvalert.model.AllServerStatus
import retrofit2.Response
import retrofit2.http.GET

interface FFIXVApi {

    @GET("ffxiv-server-status")
    suspend fun getServerStatus(): Response<AllServerStatus>

}