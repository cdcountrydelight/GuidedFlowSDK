package com.cd.uielementmanager.presentation.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.domain.contents.BoundsContent
import com.cd.uielementmanager.domain.contents.CompleteFlowResponseContent
import com.cd.uielementmanager.domain.contents.CompleteQnAContent
import com.cd.uielementmanager.domain.contents.CompleteQnaResponseContent
import com.cd.uielementmanager.domain.contents.FlowDetailsResponseContent
import com.cd.uielementmanager.domain.contents.FlowListResponseContent
import com.cd.uielementmanager.domain.contents.PositionContent
import com.cd.uielementmanager.domain.contents.QnaResponseContent
import com.cd.uielementmanager.domain.contents.SizeContent
import com.cd.uielementmanager.domain.contents.TrainingStepContent
import com.cd.uielementmanager.domain.contents.UIElementContent
import com.cd.uielementmanager.domain.domain_utils.AppErrorCodes
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.use_cases.CompleteQnAUseCase
import com.cd.uielementmanager.domain.use_cases.GetFlowsListUseCase
import com.cd.uielementmanager.domain.use_cases.GetQnAUseCase
import com.cd.uielementmanager.domain.use_cases.GetTrainingFlowUseCase
import com.cd.uielementmanager.domain.use_cases.SendPackageNameUseCase
import com.cd.uielementmanager.domain.use_cases.SendUIElementsUseCase
import com.cd.uielementmanager.domain.use_cases.TrainingCompletedUseCase
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.mapToDataUiResponseStatus
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel for UI element tracking following clean architecture
 * Matches the pattern from training flow feature
 */


class UIElementViewModel() : ViewModel() {

    // State flows for tracked elements - organized by screen name
    private val _trackedElements =
        MutableStateFlow<Map<String, Map<String, UIElementContent>>>(emptyMap())

    internal val trackedElements: StateFlow<Map<String, Map<String, UIElementContent>>> =
        _trackedElements.asStateFlow()


    private val _sendUiElementsStateFlow: MutableStateFlow<DataUiResponseStatus<Unit>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

    internal val sendUiElementsStateFlow = _sendUiElementsStateFlow.asStateFlow()

    // Training flow state management
    private val _trainingFlowStateFlow =
        MutableStateFlow<DataUiResponseStatus<Unit>>(DataUiResponseStatus.none())

    internal val trainingFlowState = _trainingFlowStateFlow.asStateFlow()

    // Current training step index
    private val _currentStepIndex = MutableStateFlow(0)

    internal val currentStepIndex = _currentStepIndex.asStateFlow()

    private var currentScreen: String? = null

    private var trainingFlowResponseMap = mutableMapOf<String, List<TrainingStepContent>>()

    internal var currentScreenStepsList = mutableStateListOf<TrainingStepContent>()

    private val _qnaStateFlow: MutableStateFlow<DataUiResponseStatus<QnaResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

    val qnaStateFlow = _qnaStateFlow.asStateFlow()

    var selectedQuestionIndex by mutableIntStateOf(0)

    private var authenticationToken: String = ""
  //   var selectedFlow: FlowDetailsResponseContent? = null

    var selectedFlowId: Int? = null
        private set


    private val _qnaCompleteStateFlow: MutableStateFlow<DataUiResponseStatus<CompleteQnaResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

     val qnaCompleteStateFlow = _qnaCompleteStateFlow.asStateFlow()


    private val _trainingCompletedStateFlow: MutableStateFlow<DataUiResponseStatus<CompleteFlowResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.none())

     val completeTrainingStateFlow = _trainingCompletedStateFlow.asStateFlow()

    private val _flowsListDetailStateFlow: MutableStateFlow<DataUiResponseStatus<List<FlowListResponseContent>>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

     val flowsListDetailStateFlow = _flowsListDetailStateFlow.asStateFlow()

    private val _flowDetailsStateFlow: MutableStateFlow<DataUiResponseStatus<FlowDetailsResponseContent>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

    val flowDetailsStateFlow = _flowDetailsStateFlow.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()


    fun setSelectedFlow(id: Int?) {
        selectedFlowId = id
    }
//        fun resetAllStates() {
//        _flowsListDetailStateFlow.value = DataUiResponseStatus.none()
//        _qnaStateFlow.value = DataUiResponseStatus.none()
//        selectedQuestionIndex = 0
//        _qnaCompleteStateFlow.value = DataUiResponseStatus.none()
//        resetCompleteTraining()
//    }
//
//    fun resetCompleteTraining() {
//        _trainingCompletedStateFlow.value = DataUiResponseStatus.none()
//    }



    fun backgroundCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    operation: suspend () -> Unit,
) {
    viewModelScope.launch(dispatcher) {
        operation()
    }
}


    fun getFlowsList(context: Context, authToken: String, packageName: String) {
        authenticationToken = authToken
        if (_flowsListDetailStateFlow.value is DataUiResponseStatus.Success) return
        _flowsListDetailStateFlow.value = DataUiResponseStatus.Companion.loading()
        backgroundCall {
            _flowsListDetailStateFlow.value =
                GetFlowsListUseCase().invoke(context, packageName, authenticationToken)
                    .mapToDataUiResponseStatus()
        }
    }

    fun refreshFlowsList(context: Context, authToken: String, packageName: String) {
        authenticationToken = authToken
        _isRefreshing.value = true
        backgroundCall {
            val result = GetFlowsListUseCase().invoke(context, packageName, authenticationToken)
                .mapToDataUiResponseStatus()
            _flowsListDetailStateFlow.value = result
            _isRefreshing.value = false
        }
    }

    fun resetFlowDetailsState() {
        _flowDetailsStateFlow.value = DataUiResponseStatus.Companion.none()
    }
    fun completeTraining(flowId: Int, context: Context) {
        _trainingCompletedStateFlow.value = DataUiResponseStatus.loading()
        backgroundCall {
            _trainingCompletedStateFlow.value =
                TrainingCompletedUseCase().invoke(context, flowId, authenticationToken)
                    .mapToDataUiResponseStatus()
        }
    }


    fun completeQnA(flowId: Int, context: Context, questionsDetails: QnaResponseContent) {
        _qnaCompleteStateFlow.value = DataUiResponseStatus.loading()
        backgroundCall {
            _qnaCompleteStateFlow.value = CompleteQnAUseCase().invoke(
                context,
                authenticationToken,
                flowId,
                questionsDetails.question.map { question ->
                    CompleteQnAContent(
                        question.questionId,
                        question.selectedOptions.map { it.optionId })
                }).mapToDataUiResponseStatus()
        }
    }

    internal fun setCurrentScreen(screen: String) {
        val previousScreen = currentScreen
        currentScreen = screen
        viewModelScope.launch {
            delay(200)
            currentScreenStepsList.clear()
            currentScreenStepsList.addAll(trainingFlowResponseMap[screen] ?: arrayListOf())
        }
        if (previousScreen != null && previousScreen != screen) {
            clearElementsForScreen(previousScreen)
        }
    }

    internal fun clearElementsForScreen(screen: String) {
        _trackedElements.update { screenMap ->
            screenMap - screen
        }
        if (currentScreen == screen) {
            currentScreen = null
        }
    }


    fun getCurrentScreen(): String? {
        return currentScreen
    }

    /**
     * Register a UI element for tracking
     * @param elementScreenName Screen name from the trackElement call
     * @param tag Unique identifier for the UI element
     * @param bounds Complete position and size information
     */
    internal fun registerElement(elementScreenName: String, tag: String, bounds: Rect) {
        val currentScreenName = currentScreen ?: return
        if (elementScreenName != currentScreenName) {
            return
        }
        val element = UIElementContent(
            tag = tag,
            bounds = BoundsContent(
                position = PositionContent(bounds.left, bounds.top),
                size = SizeContent(bounds.width.toInt(), bounds.height.toInt())
            ),
        )
        _trackedElements.update { screenMap ->
            val screenElements = screenMap[currentScreenName] ?: emptyMap()
            screenMap + (currentScreenName to (screenElements + (tag to element)))
        }
    }


    /**
     * Get tracked elements for the current screen
     */
    internal fun getTrackedElements(): Map<String, UIElementContent> {
        val screenName = currentScreen ?: return emptyMap()
        return _trackedElements.value[screenName] ?: emptyMap()
    }


    /**
     * Extract and send UI data to server using clean architecture
     */
    internal fun sendUIElements(context: Context, imageBitmap: Bitmap, packageName: String) {
        val currentScreen = currentScreen
        if (_sendUiElementsStateFlow.value is DataUiResponseStatus.Loading || currentScreen == null) {
            return
        }
        _sendUiElementsStateFlow.value = DataUiResponseStatus.loading()
        viewModelScope.launch {
            _sendUiElementsStateFlow.value = try {
                val sendPackageNameUseCase = SendPackageNameUseCase()
                val response = sendPackageNameUseCase.invoke(packageName, context)
                when (response) {
                    is DataResponseStatus.Failure -> {
                        DataUiResponseStatus.Companion.failure(
                            response.errorMessage,
                            response.errorCode
                        )
                    }

                    is DataResponseStatus.Success -> {
                        val flowId = response.data.flowId
                        if (flowId == null) {
                            DataUiResponseStatus.Companion.failure(
                                "Flow id can't be null",
                                AppErrorCodes.UNKNOWN_ERROR
                            )
                        } else {
                            val screenshotFile = createFileFromBitmap(imageBitmap, context)
                            val screenshotPart = MultipartBody.Part.createFormData(
                                "screenshot",
                                screenshotFile.name,
                                screenshotFile.asRequestBody("image/png".toMediaTypeOrNull())
                            )

                            val screenNamePart =
                                currentScreen.toRequestBody("text/plain".toMediaType())

                            val timestampPart =
                                System.currentTimeMillis().toString()
                                    .toRequestBody("text/plain".toMediaType())
                            val screenInfoPart = createScreenInfo(context)

                            val guidedFlowElements = getTrackedElements().values.toList()
                            val elementsJson = Gson().toJson(guidedFlowElements)
                            val elementsPart =
                                elementsJson.toRequestBody("text/plain".toMediaType())

                            val sendUIElementsUseCase = SendUIElementsUseCase()
                            val response = sendUIElementsUseCase.invoke(
                                flowId = flowId,
                                screenshotPart,
                                screenNamePart,
                                timestampPart,
                                screenInfoPart,
                                elementsPart,
                                context
                            ).mapToDataUiResponseStatus()

                            if (response is DataUiResponseStatus.Success) {
                                screenshotFile.delete()
                            }
                            response
                        }
                    }
                }
            } catch (exception: Exception) {
                DataUiResponseStatus.failure(
                    exception.localizedMessage ?: exception.message ?: "",
                    AppErrorCodes.UNKNOWN_ERROR
                )
            }
        }
    }

    private fun createScreenInfo(context: Context): RequestBody {
        val displayMetrics = context.resources.displayMetrics
        val gson = Gson()
        val screenInfoJson = gson.toJson(
            mapOf("width" to displayMetrics.widthPixels, "height" to displayMetrics.heightPixels)
        )
        return screenInfoJson.toRequestBody("text/plain".toMediaType())
    }


    fun getQuestionsList(flowId: Int, context: Context) {
        _qnaStateFlow.value = DataUiResponseStatus.loading()
        selectedQuestionIndex = 0
        backgroundCall {
            _qnaStateFlow.value =
                GetQnAUseCase().invoke(context, authenticationToken, flowId)
                    .mapToDataUiResponseStatus()
        }
    }

    /**
     * Fetch training flow data from server
     * @param context Application context
     */
    internal fun fetchTrainingFlow(context: Context, packageName: String, authToken: String) {
        viewModelScope.launch {
            _trainingFlowStateFlow.value = DataUiResponseStatus.loading()
            try {
                val getTrainingFlowUseCase = GetTrainingFlowUseCase()
                val response = getTrainingFlowUseCase.invoke(context, packageName, authToken)
                    .mapToDataUiResponseStatus()
                // Reset step index when new flow is loaded
                if (response is DataUiResponseStatus.Success) {
                    val responseMap = response.data
                        .flatMap { it.steps }         // all steps from all flows
                        .groupBy { it.screenName }
                    trainingFlowResponseMap.clear()
                    trainingFlowResponseMap.putAll(responseMap)
                    _currentStepIndex.value = 0
                } else {
                    _currentStepIndex.value = -1
                }
                _trainingFlowStateFlow.value = DataUiResponseStatus.success(Unit)
            } catch (exception: Exception) {
                _trainingFlowStateFlow.value = DataUiResponseStatus.failure(
                    exception.localizedMessage ?: exception.message ?: "",
                    AppErrorCodes.UNKNOWN_ERROR
                )
            }
        }
    }

    /**
     * Navigate to next training step
     */
    fun nextTrainingStep() {
        _currentStepIndex.update { current ->
            if (current < currentScreenStepsList.size - 1) current + 1 else current
        }
    }

    override fun onCleared() {
        HttpClientManager.clearInstance()
        super.onCleared()
    }

    private fun createFileFromBitmap(bitmap: Bitmap, context: Context): File {
        val timestamp = System.currentTimeMillis()
        val file = File(context.cacheDir, "screenshot_$timestamp.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}