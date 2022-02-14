package com.example.ffixvalert.api

import com.example.ffixvalert.constant.BaseUrl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy{
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BaseUrl.FFIXV_SERVER_STATUE_URL)
            .build()
    }

    val ffixvApi: FFIXVApi by lazy{
        retrofit.create(FFIXVApi::class.java)
    }

}