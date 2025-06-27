package com.example.flighttrackerques2.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightApiService {
    @GET("flights")
    suspend fun getFlight(
        @Query("access_key") accessKey: String,
        @Query("flight_iata") flightNumber: String
    ): FlightResponse

    @GET("flights")
    suspend fun getFlightsBetween(
        @Query("access_key") accessKey: String,
        @Query("dep_iata") depIata: String,
        @Query("arr_iata") arrIata: String
    ): FlightResponse

}



object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Shows full URL, headers, and response body
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aviationstack.com/v1/") // or https if your plan supports it
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: FlightApiService = retrofit.create(FlightApiService::class.java)
}