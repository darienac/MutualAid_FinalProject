package com.example.mutualaid_finalproject.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Define the endpoints and parameters for the Google Maps Distance Matrix API.
interface DistanceMatrixService {
    @GET("distancematrix/json")
    suspend fun getDistance(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("mode") mode: String = "driving",
        @Query("key") apiKey: String,
        @Query("units") units: String = "imperial"    // Units: 'imperial' for miles, 'metric' for km
    ): DistanceMatrixResponse
}

// Define data classes to model the response from the API.
data class DistanceMatrixResponse(
    val rows: List<Row>
)

data class Row(
    val elements: List<Element>
)

data class Element(
    val distance: Distance,
    val duration: Duration,
    val status: String
)

data class Distance(val text: String, val value: Int)
data class Duration(val text: String, val value: Int)

class DistanceCalculator {

    private val API_KEY = "AIzaSyDu9bU6T6VHCmphked1DR7zgfRcgDN4mQw"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val service: DistanceMatrixService = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(DistanceMatrixService::class.java)

    // Function to calculate distance
    suspend fun getDistance(origin: String, destination: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getDistance(
                    origins = origin,
                    destinations = destination,
                    apiKey = API_KEY
                )
                response.rows.firstOrNull()
                    ?.elements?.firstOrNull()
                    ?.distance?.text
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun getDistanceAsync(origin: String, destination: String, id: String, onResult: (distance: String?, id: String) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            onResult(getDistance(origin, destination), id)
        }
    }
}