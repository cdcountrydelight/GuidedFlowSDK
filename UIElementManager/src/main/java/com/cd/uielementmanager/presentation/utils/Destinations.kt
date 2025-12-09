package com.cd.uielementmanager.presentation.utils

import kotlinx.serialization.Serializable

@Serializable
internal object FlowListScreenDestination


@Serializable
internal object QnAScreenDestination

@Serializable
internal class CompletedTrainingScreenDestination(val calculatedScore: Double?)