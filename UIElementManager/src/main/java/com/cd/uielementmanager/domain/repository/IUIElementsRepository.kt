package com.cd.uielementmanager.domain.repository

import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.domain.contents.CompleteFlowResponseContent
import com.cd.uielementmanager.domain.contents.CompleteQnAContent
import com.cd.uielementmanager.domain.contents.CompleteQnaResponseContent
import com.cd.uielementmanager.domain.contents.FlowListResponseContent
import com.cd.uielementmanager.domain.contents.QnaResponseContent
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
    suspend fun sendPackageName(packageName: String): DataResponseStatus<PackageNameResponse>

    suspend fun getQnADetails(flowId: Int): DataResponseStatus<QnaResponseContent>

    //guided flow
    suspend fun getFlowsList(packageName: String): DataResponseStatus<List<FlowListResponseContent>>

    suspend fun getTrainingFlow(packageName: String, authToken: String):DataResponseStatus<List<TrainingFlowContent>>

    suspend fun completeQnA(
        flowId: Int,
        completeQnAList: List<CompleteQnAContent>,
    ): DataResponseStatus<CompleteQnaResponseContent>

    suspend fun completeTraining(flowId: Int): DataResponseStatus<CompleteFlowResponseContent>


    suspend fun startFlow(flowId: String): DataResponseStatus<Unit>


}