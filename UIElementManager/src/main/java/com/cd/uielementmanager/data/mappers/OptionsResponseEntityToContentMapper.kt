package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.OptionsEntity
import com.cd.uielementmanager.domain.contents.OptionsContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

internal class OptionsResponseEntityToContentMapper : IBaseMapper<OptionsEntity, OptionsContent?> {
    override fun mapData(data: OptionsEntity): OptionsContent? {
        return if (data.optionId == null || data.option.isNullOrBlank()) {
            null
        } else {
            OptionsContent(data.optionId, data.option)
        }
    }
}