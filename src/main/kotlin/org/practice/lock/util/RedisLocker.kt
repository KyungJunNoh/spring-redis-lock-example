package org.practice.lock.util

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.TimeUnit
import org.springframework.integration.redis.util.RedisLockRegistry

/**
 * RedisLocker 클래스는 RedisLockRegistry를 사용하여 동시성 제어를 위한 락(lock)
 *
 * @property registryKeyPrefix Redis 레지스트리의 키(key)에 접두사(prefix)로 사용될 문자열
 * @property defaultObtainWaitingTimeSec 락 획득을 시도할 때 기본 대기 시간(초). default 5초
 * @property lockRegistry 락(lock)을 관리하는 RedisLockRegistry
 */
class RedisLocker(
    private val registryKeyPrefix: String,
    private val defaultObtainWaitingTimeSec: Int = 5,
    private val lockRegistry: RedisLockRegistry,
) {
    private val logger = KotlinLogging.logger { }

    /**
     * 지정된 키(key)에 대한 락(lock)을 획득하고, 주어진 람다 함수를 실행
     *
     * @param key 락(lock)을 획득할 키(key)로 사용될 문자열
     * @param obtainWaitingTimeSec 락을 획득하기 위해 대기할 최대 시간(초). default [defaultObtainWaitingTimeSec]
     * @param runnable 락(lock)을 획득한 후 실행할 람다 함수. 실행 결과를 반환.
     * @return 람다 함수의 실행 결과를 반환. 락 획득에 실패한 경우 null을 반환.
     */
    fun <T> lock(
        key: String,
        obtainWaitingTimeSec: Int = defaultObtainWaitingTimeSec,
        runnable: () -> T,
    ): T? {
        var t: T? = null
        val lock = lockRegistry.obtain("$registryKeyPrefix:$key")
        if (lock.tryLock(obtainWaitingTimeSec.toLong(), TimeUnit.SECONDS)) {
            try {
                logger.trace { "LockStart - $registryKeyPrefix:$key" }
                t = runnable()
                logger.trace { "LockEnd - $registryKeyPrefix:$key" }
            } finally {
                lock.unlock()
            }
        } else {
            logger.trace { "LockFailed - $registryKeyPrefix:$key" }
        }
        return t
    }
}
