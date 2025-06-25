package com.netwatcher.polaris.data.remote

import com.netwatcher.polaris.domain.model.User
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

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

    @PUT("api/mobile/network-data/sync-status/")
    suspend fun updateSyncStatus(
        @Header("Authorization") token: String,
        @Body syncRequest: SyncStatusRequest
    ): Response<Unit>

    @GET("api/users/profile/")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<User>
}

data class SyncStatusRequest(val syncedIds: List<Long>)