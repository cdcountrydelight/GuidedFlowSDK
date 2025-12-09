package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.QuestionResponseEntity
import com.cd.uielementmanager.domain.contents.QuestionResponseContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

internal class QuestionResponseEntityToContentMapper :
    IBaseMapper<QuestionResponseEntity?, QuestionResponseContent?> {
    override fun mapData(data: QuestionResponseEntity?): QuestionResponseContent? {
        val optionsMapper = OptionsResponseEntityToContentMapper()
        val mappedOptions = data?.options?.mapNotNull { optionsMapper.mapData(it) }
        return if (data?.questionId == null || data.question.isNullOrBlank() || mappedOptions == null) {
            null
        } else {
            QuestionResponseContent(
                data.questionId,
                data.question,
                mappedOptions,
                data.isMsq ?: false,
                data.isRequired ?: true
            )
        }
    }
}