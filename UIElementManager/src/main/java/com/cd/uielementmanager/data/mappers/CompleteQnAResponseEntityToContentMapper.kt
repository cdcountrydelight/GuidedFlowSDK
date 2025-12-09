package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.CompleteQnAResponseEntity
import com.cd.uielementmanager.domain.contents.CompleteQnaResponseContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

class CompleteQnAResponseEntityToContentMapper :
    IBaseMapper<CompleteQnAResponseEntity, CompleteQnaResponseContent> {
    override fun mapData(data: CompleteQnAResponseEntity): CompleteQnaResponseContent {
        return CompleteQnaResponseContent(data.calculatedScore)
    }
}