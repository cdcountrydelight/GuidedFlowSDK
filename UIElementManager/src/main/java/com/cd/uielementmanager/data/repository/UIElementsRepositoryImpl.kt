package com.cd.uielementmanager.data.repository

import com.cd.uielementmanager.data.entities.CompleteQnARequestEntity
import com.cd.uielementmanager.data.entities.CompleteQuestionsRequestEntity
import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.data.mappers.CompleteFlowResponseMapper
import com.cd.uielementmanager.data.mappers.CompleteQnAResponseEntityToContentMapper
import com.cd.uielementmanager.data.mappers.QnAResponseEntityToContentMapper
import com.cd.uielementmanager.data.mappers.TrainingFlowMapper
import com.cd.uielementmanager.data.network.NetworkCallHelper.networkCall
import com.cd.uielementmanager.data.network.NetworkCallHelper.networkCallForList
import com.cd.uielementmanager.data.network.NetworkCallHelper.networkCallForUpload
import com.cd.uielementmanager.data.network.UIElementsApiService
import com.cd.uielementmanager.domain.contents.CompleteFlowResponseContent
import com.cd.uielementmanager.domain.contents.CompleteQnAContent
import com.cd.uielementmanager.domain.contents.CompleteQnaResponseContent
import com.cd.uielementmanager.domain.contents.QnaResponseContent
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.domain_utils.AppErrorCodes
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Repository implementation for UI elements operations using Retrofit
 * Following clean architecture principles
 */
internal class UIElementsRepositoryImpl(private val apiService: UIElementsApiService) :
    IUIElementsRepository {

    override suspend fun sendUIElementsWithScreenshot(
        flowId: Int?,
        screenshotPart: MultipartBody.Part,
        screenNamePart: RequestBody,
        timestampPart: RequestBody,
        screenInfoPart: RequestBody,
        elementsPart: RequestBody,
    ): DataResponseStatus<Unit> {
        return networkCallForUpload {
            apiService.uploadUIElementsSnapshot(
                flowId = flowId,
                screenshot = screenshotPart,
                screenName = screenNamePart,
                timestamp = timestampPart,
                screenInfo = screenInfoPart,
                elements = elementsPart
            )
        }
    }


    override suspend fun sendPackageName(packageName: String): DataResponseStatus<PackageNameResponse> {
        return networkCall {
            apiService.uploadPackageName(packageName)
        }
    }

    override suspend fun startFlow(flowId: String): DataResponseStatus<Unit> {
        return networkCall {
            apiService.startFlow(flowId)
        }
    }

    override suspend fun getQnADetails(flowId: Int): DataResponseStatus<QnaResponseContent> {

        val mapper = QnAResponseEntityToContentMapper()
        val response = networkCall(mapper) {
            apiService.getQuizFlow(flowId)
        }

        return when (response) {
            is DataResponseStatus.Success -> {
                val data = response.data
                if (data == null) {
                    DataResponseStatus.failure(
                        errorMessage = "No QnA data found",
                        errorCode = AppErrorCodes.UNKNOWN_ERROR
                    )
                } else {
                    DataResponseStatus.success(data)
                }
            }

            is DataResponseStatus.Failure -> {
                response
            }
        }
    }


    override suspend fun completeQnA(
        flowId: Int,
        completeQnAList: List<CompleteQnAContent>,
    ): DataResponseStatus<CompleteQnaResponseContent> {

        val requestBody = CompleteQnARequestEntity(
            completeQnAList.map {
                CompleteQuestionsRequestEntity(
                    it.question,
                    it.options
                )
            }
        )

        return networkCall(CompleteQnAResponseEntityToContentMapper()) {
            apiService.completeQnA(flowId, requestBody)
        }
    }

    override suspend fun completeTraining(
        flowId: Int,
    ): DataResponseStatus<CompleteFlowResponseContent> {

        return networkCall(CompleteFlowResponseMapper()) {
            apiService.completeTraining(flowId)
        }
    }

    override suspend fun getTrainingFlow(
        packageName: String,
    ): DataResponseStatus<List<TrainingFlowContent>> {
        return networkCallForList(TrainingFlowMapper()) {
            apiService.getTrainingFlow(packageName)
        }
    }
}