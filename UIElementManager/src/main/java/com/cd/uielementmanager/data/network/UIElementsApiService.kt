package com.cd.uielementmanager.data.network

import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.data.entities.TrainingFlowEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for UI elements operations
 */
internal interface UIElementsApiService {

    @Multipart
    @POST("live-guided-screenshots/{flow_id}/upload/")
    suspend fun uploadUIElementsSnapshot(
        @Path("flow_id") flowId: Int?,
        @Part screenshot: MultipartBody.Part,
        @Part("screen_name") screenName: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("screen_info") screenInfo: RequestBody,
        @Part("elements") elements: RequestBody
    ): Response<ResponseBody>

    @GET("guided-flows/")
    suspend fun getTrainingFlow(@Query("app_package") packageName: String): Response<List<TrainingFlowEntity>>

    @GET("guided-flows/active/{packageName}/")
    suspend fun uploadPackageName(
        @Path("packageName") packageName: String
    ): Response<PackageNameResponse>
}