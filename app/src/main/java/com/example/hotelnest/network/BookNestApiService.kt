package com.example.hotelnest.network

import com.example.hotelnest.data.CityData
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl("https://training-uploads.internshala.com")
    .build()

interface BookNestApiService {
    @GET("/android/hotelbooking/places.json")
    suspend fun getItems(): List<CityData>
}

object BookNestApi{
    val retrofitService: BookNestApiService by lazy {
        retrofit.create(BookNestApiService::class.java)
    }
}