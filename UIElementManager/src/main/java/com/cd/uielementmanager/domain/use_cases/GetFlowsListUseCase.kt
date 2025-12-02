package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.contents.FlowListResponseContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository

internal class GetFlowsListUseCase {
    suspend fun invoke(
        context: Context,
        packageName: String,
        authToken: String
    ): DataResponseStatus<List<FlowListResponseContent>> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.getFlowsList(packageName,authToken)
    }
}