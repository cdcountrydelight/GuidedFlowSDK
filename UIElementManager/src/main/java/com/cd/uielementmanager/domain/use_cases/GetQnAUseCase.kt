package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.contents.QnaResponseContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository

internal class GetQnAUseCase {
    suspend fun invoke(
        context: Context,
        authToken: String,
        flowId: Int
    ): DataResponseStatus<QnaResponseContent> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.getQnADetails(flowId, authToken)
    }
}