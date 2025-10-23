package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository

internal class SendPackageNameUseCase {

    suspend fun invoke(
        packageName: String,
        context: Context
    ): DataResponseStatus<PackageNameResponse> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.sendPackageName(packageName)
    }
}