package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.CompleteFlowResponseEntity
import com.cd.uielementmanager.domain.contents.CompleteFlowResponseContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

class CompleteFlowResponseMapper :
    IBaseMapper<CompleteFlowResponseEntity, CompleteFlowResponseContent> {

    override fun mapData(data: CompleteFlowResponseEntity): CompleteFlowResponseContent {
        return CompleteFlowResponseContent(
            message = data.message,
            flowId = data.flowId,
            flowName = data.flowName,
            completedAt = data.completedAt,
            userId = data.userId
        )
    }
}
