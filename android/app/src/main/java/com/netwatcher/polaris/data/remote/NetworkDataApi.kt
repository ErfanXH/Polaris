package com.netwatcher.polaris.data.remote

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NetworkDataApi {
    @POST("api/mobile/measurement/")
    suspend fun uploadNetworkData(
        @Header("Authorization") token: String,
        @Body data: Any
    ): Response<Unit>

    @POST("api/mobile/bulk_upload/measurement/")
    suspend fun uploadNetworkDataBatch(
        @Header("Authorization") token: String,
        @Body data: RequestBody
    ): Response<Unit>
}