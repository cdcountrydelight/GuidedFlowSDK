package com.cd.uielementmanager.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    internal fun backgroundCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        operation: suspend () -> Unit,
    ) {
        viewModelScope.launch(dispatcher) {
            operation()
        }
    }
}