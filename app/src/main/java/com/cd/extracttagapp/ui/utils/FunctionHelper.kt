package com.cd.extracttagapp.ui.utils

import android.content.Context
import com.cd.extracttagapp.R

object FunctionHelper {
    fun Context.getErrorMessage(errorMessage: String, errorCode: Int): String {
        return when (errorCode) {
            AppErrorCodes.NO_INTERNET_CONNECTION_ERROR -> {
                getString(R.string.no_internet_connection_please_make_sure_your_device_is_connected_to_an_active_internet_connection)
            }


            else -> {
                errorMessage.ifBlank {
                    getString(R.string.something_went_wrong_please_try_again_later)
                }
            }
        }
    }
}