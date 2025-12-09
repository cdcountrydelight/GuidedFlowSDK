package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.FlowListResponseEntity
import com.cd.uielementmanager.domain.contents.FlowListResponseContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

class FlowListMapper :
    IBaseMapper<List<FlowListResponseEntity>, List<FlowListResponseContent>> {

    private val userProgressMapper = UserProgressDetailsMapper()

    override fun mapData(data: List<FlowListResponseEntity>): List<FlowListResponseContent> {
        return data.mapNotNull { entity ->
            if (entity.isActive != true || entity.id == null || entity.stepCount == 0) {
                null
            } else {
                FlowListResponseContent(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    isActive = true,
                    stepCount = entity.stepCount,
                    userProgress = entity.userProgress?.let { userProgressMapper.mapData(it) }
                )
            }
        }
    }
}
