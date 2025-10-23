package com.cd.uielementmanager.data.network

import android.content.Context
import com.cd.uielementmanager.data.network.NetworkCallHelper.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Network interceptor to check internet connectivity and add headers
 * Following the same pattern as training flow feature
 *
 */
internal class AppNetworkInterceptorImpl(
    private val context: Context,
    private val authToken: String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder().addHeader(
            "Authorization",
            "Bearer $authToken"
        ).build()
        if (!context.isNetworkAvailable()) {
            throw NoInternetConnectionException()
        }

        val response = chain.proceed(newRequest)

        return response
    }
}