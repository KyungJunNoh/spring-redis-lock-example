package org.practice.lock.service

import org.practice.lock.util.RedisLockerComponent
import org.springframework.stereotype.Service

@Service
class BoardLikeFacade(
    private val boardLikeService: BoardLikeService,
    redisLockerService: RedisLockerComponent,
) {

    private val locker = redisLockerService.createLocker(
        registryKey = "board-like",
        defaultObtainLockWaitingTimeSec = 5,
    )

    fun increase(boardId: Long) {
        locker.lock(boardId.toString()) {
            boardLikeService.increase(boardId)
        } ?: throw Exception("실패")
    }

    fun decrease(boardId: Long) {
        locker.lock(boardId.toString()) {
            boardLikeService.decrease(boardId)
        } ?: throw Exception("실패")
    }
}
