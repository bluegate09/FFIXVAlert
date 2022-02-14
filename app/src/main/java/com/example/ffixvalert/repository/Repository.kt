package com.example.ffixvalert.repository

import com.example.ffixvalert.api.RetrofitInstance
import com.example.ffixvalert.model.AllServerStatus
import retrofit2.Response

class Repository {
    suspend fun getServerStatus(): Response<AllServerStatus>{
        return RetrofitInstance.ffixvApi.getServerStatus()
    }

}