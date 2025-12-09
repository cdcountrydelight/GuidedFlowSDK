package com.cd.uielementmanager.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cd.uielementmanager.R
import com.cd.uielementmanager.domain.contents.FlowListResponseContent
import com.cd.uielementmanager.presentation.beans.ButtonHandlerBean
import com.cd.uielementmanager.presentation.composables.EmptySection
import com.cd.uielementmanager.presentation.composables.ErrorAlertDialog
import com.cd.uielementmanager.presentation.composables.LoadingSection
import com.cd.uielementmanager.presentation.composables.SpacerHeight8s
import com.cd.uielementmanager.presentation.composables.SpacerWidth4s
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.getErrorMessage
import com.cd.uielementmanager.presentation.viewmodels.QuizViewModel


@Composable
internal fun FlowListScreen(
    appName: String,
    packageName: String,
    viewModel: QuizViewModel,
    onFlowSelected: () -> Unit,
    onBackClicked: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HandleFlowListStateFlow(
            viewModel = viewModel,
            appName = appName,
            packageName = packageName,
            onBackClicked = onBackClicked,
            onFlowSelected = onFlowSelected
        )

    }
    LaunchedEffect(Unit) {
        viewModel.getFlowsList(context, packageName)
    }
}

@Composable
private fun HandleFlowListStateFlow(
    appName: String,
    packageName: String,
    viewModel: QuizViewModel,
    onBackClicked: () -> Unit,
    onFlowSelected: () -> Unit,
) {
    val flowListResponseState = viewModel.flowsListDetailStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isResponseHandled by remember { mutableStateOf(false) }
    when (val response = flowListResponseState.value) {
        is DataUiResponseStatus.Loading -> {
            isResponseHandled = false
            LoadingSection()
        }

        is DataUiResponseStatus.Success -> {
            if (response.data.isEmpty()) {
                EmptySection(
                    message = stringResource(R.string.no_training_flows_available),
                    subtitle = stringResource(
                        R.string.there_are_currently_no_training_flows_configured_for_please_check_back_later_or_contact_your_administrator,
                        appName
                    ),
                    actionText = stringResource(R.string.go_back),
                    onActionClick = onBackClicked
                )
            } else {
                FlowsList(
                    appName = appName,
                    flows = response.data,
                    viewModel = viewModel,
                    packageName = packageName,
                    onFlowSelected = onFlowSelected
                )
            }
        }

        is DataUiResponseStatus.Failure -> {
            if (!isResponseHandled) {
                ErrorAlertDialog(
                    errorMessage = context.getErrorMessage(
                        response.errorMessage,
                        response.errorCode
                    ),
                    positiveButton = ButtonHandlerBean(
                        buttonText = stringResource(R.string.ok),
                        onButtonClicked = {
                            isResponseHandled = true
                            viewModel.getFlowsList(context, packageName)
                        }
                    ),
                    negativeButton = ButtonHandlerBean(
                        buttonText = stringResource(R.string.cancel),
                        onButtonClicked = {
                            isResponseHandled = true
                            onBackClicked()
                        }
                    )
                )
            }
        }

        else -> {}
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlowsList(
    appName: String,
    flows: List<FlowListResponseContent>,
    viewModel: QuizViewModel,
    packageName: String,
    onFlowSelected: () -> Unit,
) {

    val context = LocalContext.current
    val isRefreshingState = viewModel.isRefreshing.collectAsStateWithLifecycle()
    PullToRefreshBox(
        isRefreshing = isRefreshingState.value,
        onRefresh = {
            viewModel.refreshFlowsList(context, packageName)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                            MaterialTheme.colorScheme.secondary
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(1000f, 1000f)
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .offset(x = (-40).dp, y = (-40).dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        )

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 40.dp, y = 20.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = CircleShape
                                )
                        )

                        HeaderSection(
                            appName, Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .padding(horizontal = 20.dp)
                                .padding(top = 16.dp)
                        )
                    }
                    StatusCard(flows)
                }
            }

            items(flows) { flow ->
                FlowItem(
                    flow = flow,
                    onClick = {
                        flow.userProgress?.isStarted = true
                        viewModel.setSelectedFlow(flow.id)
                        onFlowSelected()

                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(appName: String, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        clip = false
                    )
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
            SpacerHeight8s()
            Text(
                text = stringResource(R.string.training_platform),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 1.sp
            )
        }
    }
}


@Composable
private fun StatusCard(flows: List<FlowListResponseContent>) {
    var height by remember {
        mutableFloatStateOf(0.0f)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = with(LocalDensity.current) { -(height / 2).toDp() })
            .onGloballyPositioned { coordinates ->
                height = coordinates.size.height.toFloat()
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusCardItem(stringResource(R.string.available_flows), flows.size)
            StatusCardItemDivider()
            val completedCount =
                flows.count { it.userProgress?.isCompleted == true }
            StatusCardItem(stringResource(R.string.completed), completedCount)
            StatusCardItemDivider()
            val inProgressCount = flows.count {
                it.userProgress?.isStarted == true &&
                        it.userProgress!!.isCompleted != true
            }//check here for this condition -> it.userProgress!!
            StatusCardItem(stringResource(R.string.pending), inProgressCount)
        }
    }
}


@Composable
private fun StatusCardItem(text: String, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$count",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusCardItemDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(
                color = MaterialTheme.colorScheme.outlineVariant
            )
    )
}

@Composable
private fun FlowItem(
    flow: FlowListResponseContent,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when {
                            flow.userProgress?.isCompleted == true -> Color(0xff40AB2C).copy(alpha = 0.1f)
                            flow.userProgress?.isStarted == true -> Color(0xffFFA726).copy(alpha = 0.1f)
                            else -> Color(0xff9E9E9E).copy(alpha = 0.1f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when {
                            flow.userProgress?.isCompleted == true -> com.cd.uielementmanager.R.drawable.complete_circle
                            flow.userProgress?.isStarted == true -> com.cd.uielementmanager.R.drawable.incomplete_circle
                            else -> com.cd.uielementmanager.R.drawable.not_started_circle
                        }
                    ),
                    contentDescription = when {
                        flow.userProgress?.isCompleted == true -> stringResource(R.string.completed)
                        flow.userProgress?.isStarted == true -> stringResource(R.string.pending)
                        else -> stringResource(R.string.not_started)
                    },
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = flow.name ?: stringResource(R.string.untitled_flow),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                SpacerWidth4s()
                Text(
                    text = flow.description?.ifBlank { stringResource(R.string.no_description_available) }
                        ?: stringResource(R.string.no_description_available),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.steps, flow.stepCount ?: 0),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}


