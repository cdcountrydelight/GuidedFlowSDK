package com.cd.uielementmanager.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.domain.contents.CompleteFlowResponseContent
import com.cd.uielementmanager.domain.contents.CompleteQnAContent
import com.cd.uielementmanager.domain.contents.CompleteQnaResponseContent
import com.cd.uielementmanager.domain.contents.QnaResponseContent
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.use_cases.CompleteQnAUseCase
import com.cd.uielementmanager.domain.use_cases.GetQnAUseCase
import com.cd.uielementmanager.domain.use_cases.GetTrainingFlowUseCase
import com.cd.uielementmanager.domain.use_cases.TrainingCompletedUseCase
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.mapToDataUiResponseStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel : BaseViewModel() {

    private val _flowsListDetailStateFlow: MutableStateFlow<DataUiResponseStatus<List<TrainingFlowContent>>> =
        MutableStateFlow(DataUiResponseStatus.none())

    internal val flowsListDetailStateFlow = _flowsListDetailStateFlow.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing = _isRefreshing.asStateFlow()

    private val _qnaStateFlow: MutableStateFlow<DataUiResponseStatus<QnaResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.none())

    val qnaStateFlow = _qnaStateFlow.asStateFlow()

    private val _qnaCompleteStateFlow: MutableStateFlow<DataUiResponseStatus<CompleteQnaResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.none())

    val qnaCompleteStateFlow = _qnaCompleteStateFlow.asStateFlow()

    private val _trainingCompletedStateFlow: MutableStateFlow<DataUiResponseStatus<CompleteFlowResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.none())

    val completeTrainingStateFlow = _trainingCompletedStateFlow.asStateFlow()

    var selectedQuestionIndex by mutableIntStateOf(0)


    var selectedFlowId: Int? = null
        private set

    fun initData(authToken: String, isProdEnv: Boolean) {
        HttpClientManager.initializeDetails(authToken, isProdEnv)
    }


    fun getFlowsList(context: Context, packageName: String) {
        if (_flowsListDetailStateFlow.value is DataUiResponseStatus.Success) return
        _flowsListDetailStateFlow.value = DataUiResponseStatus.loading()
        backgroundCall {
            _flowsListDetailStateFlow.value =
                GetTrainingFlowUseCase().invoke(context, packageName).mapToDataUiResponseStatus()
        }
    }

    fun setSelectedFlow(id: Int?) {
        selectedFlowId = id
    }

    fun refreshFlowsList(context: Context, packageName: String) {
        _isRefreshing.value = true
        backgroundCall {
            val result =
                GetTrainingFlowUseCase().invoke(context, packageName).mapToDataUiResponseStatus()
            _flowsListDetailStateFlow.value = result
            _isRefreshing.value = false
        }
    }

    fun getQuestionsList(flowId: Int, context: Context) {
        _qnaStateFlow.value = DataUiResponseStatus.loading()
        selectedQuestionIndex = 0
        backgroundCall {
            _qnaStateFlow.value =
                GetQnAUseCase().invoke(context, flowId)
                    .mapToDataUiResponseStatus()
        }
    }

    fun completeQnA(flowId: Int, context: Context, questionsDetails: QnaResponseContent) {
        _qnaCompleteStateFlow.value = DataUiResponseStatus.loading()
        backgroundCall {
            _qnaCompleteStateFlow.value = CompleteQnAUseCase().invoke(
                context,
                flowId,
                questionsDetails.question.map { question ->
                    CompleteQnAContent(
                        question.questionId,
                        question.selectedOptions.map { it.optionId })
                }).mapToDataUiResponseStatus()
        }
    }

    fun completeTraining(flowId: Int, context: Context) {
        _trainingCompletedStateFlow.value = DataUiResponseStatus.loading()
        backgroundCall {
            _trainingCompletedStateFlow.value =
                TrainingCompletedUseCase().invoke(context, flowId)
                    .mapToDataUiResponseStatus()
        }
    }

    fun resetAllStates() {
        _qnaStateFlow.value = DataUiResponseStatus.none()
        selectedQuestionIndex = 0
        _qnaCompleteStateFlow.value = DataUiResponseStatus.none()
        _trainingCompletedStateFlow.value = DataUiResponseStatus.none()
    }

}