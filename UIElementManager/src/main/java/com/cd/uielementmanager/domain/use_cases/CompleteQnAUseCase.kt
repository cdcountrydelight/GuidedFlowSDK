package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.contents.CompleteQnAContent
import com.cd.uielementmanager.domain.contents.CompleteQnaResponseContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository

class CompleteQnAUseCase {
    suspend fun invoke(
        context: Context,
        flowId: Int,
        completeQnAContent: List<CompleteQnAContent>
    ): DataResponseStatus<CompleteQnaResponseContent> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.completeQnA(flowId,completeQnAContent)
    }
}