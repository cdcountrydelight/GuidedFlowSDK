package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.UserProgressResponseEntity
import com.cd.uielementmanager.domain.contents.UserProgressResponseContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

class UserProgressDetailsMapper :
    IBaseMapper<UserProgressResponseEntity, UserProgressResponseContent> {
    override fun mapData(data: UserProgressResponseEntity): UserProgressResponseContent {
        return UserProgressResponseContent(
            isStarted = data.isStarted,
            isCompleted = data.isCompleted,
            startedAt = data.startedAt,
            completedAt = data.completedAt
        )
    }
}
