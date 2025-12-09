package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.TrainingFlowEntity
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

/**
 * Mapper to convert TrainingFlowEntity (data layer) to TrainingFlow (domain layer)
 */
internal class TrainingFlowMapper : IBaseMapper<TrainingFlowEntity, TrainingFlowContent> {

    private val trainingStepMapper = TrainingStepMapper()

    private val userProgressMapper = UserProgressDetailsMapper()

    override fun mapData(data: TrainingFlowEntity): TrainingFlowContent {
        return TrainingFlowContent(
            id = data.id,
            name = data.name,
            stepCount = data.stepCount,
            steps = data.steps?.mapNotNull { trainingStepMapper.mapData(it) }
                ?.sortedBy { it.stepNumber } ?: arrayListOf(),
            description = data.description,
            isActive = data.isActive,
            userProgress = data.userProgress?.let { userProgressMapper.mapData(it) }
        )
    }
}