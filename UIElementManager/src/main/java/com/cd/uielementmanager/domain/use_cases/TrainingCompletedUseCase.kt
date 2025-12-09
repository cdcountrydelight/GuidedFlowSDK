package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.contents.CompleteFlowResponseContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository

class TrainingCompletedUseCase {

    suspend fun invoke(
        context: Context,
        flowId: Int
    ): DataResponseStatus<CompleteFlowResponseContent> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.completeTraining(flowId)
    }
}