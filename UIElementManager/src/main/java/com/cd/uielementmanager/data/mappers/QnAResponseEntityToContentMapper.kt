package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.QnaResponseEntity
import com.cd.uielementmanager.domain.contents.QnaResponseContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

internal class QnAResponseEntityToContentMapper :
    IBaseMapper<QnaResponseEntity, QnaResponseContent?> {
    override fun mapData(data: QnaResponseEntity): QnaResponseContent? {
        val questionMapper = QuestionResponseEntityToContentMapper()
        val mappedQuestion = data.question?.mapNotNull { questionMapper.mapData(it) }
        return if (data.id == null || data.flowId == null || mappedQuestion == null) {
            null
        } else {
            QnaResponseContent(data.id, mappedQuestion, data.flowId,data.calculatedScore)
        }
    }
}