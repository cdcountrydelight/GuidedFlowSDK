package com.cd.uielementmanager.domain.repository

import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Repository interface for UI elements operations
 * Following clean architecture principles - domain layer defines the contract
 */
internal interface IUIElementsRepository {

    /**
     * Send UI elements with screenshot to the server
     *
     * @param screenshotPart Screenshot file as multipart
     * @param screenNamePart Screen name as request body
     * @param timestampPart Timestamp as request body
     * @param screenInfoPart Screen information as request body
     * @param elementsPart UI elements data as request body
     * @return DataResponseStatus with extraction response
     */
    suspend fun sendUIElementsWithScreenshot(
        flowId: Int?,
        screenshotPart: MultipartBody.Part,
        screenNamePart: RequestBody,
        timestampPart: RequestBody,
        screenInfoPart: RequestBody,
        elementsPart: RequestBody
    ): DataResponseStatus<Unit>
    
    /**
     * Get training flow data by ID
     *
     * @return DataResponseStatus with TrainingFlow data
     */
    suspend fun getTrainingFlow(packageName: String):DataResponseStatus<List<TrainingFlowContent>>

    suspend fun sendPackageName(packageName: String): DataResponseStatus<PackageNameResponse>


}