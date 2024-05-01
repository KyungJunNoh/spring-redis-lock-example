package org.practice.lock.util

import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.stereotype.Component

/**
 * RedisLockerService 클래스는 RedisLocker를 생성하는 컴포넌트
 *
 * @property redisLockRegistry [RedisLockRegistry] 인스턴스로, 락(lock)을 관리
 */
@Component
class RedisLockerComponent(
    private val redisLockRegistry: RedisLockRegistry,
) {

    fun createLocker(registryKey: String, defaultObtainLockWaitingTimeSec: Int = 5) =
        RedisLocker(registryKey, defaultObtainLockWaitingTimeSec, redisLockRegistry)
}
