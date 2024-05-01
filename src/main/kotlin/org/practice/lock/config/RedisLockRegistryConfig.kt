package org.practice.lock.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.integration.redis.util.RedisLockRegistry

@Configuration
class RedisLockRegistryConfig(
    private val redisConnectionFactory: RedisConnectionFactory
) {
    companion object {
        const val REGISTRY_PREFIX = "example:lock"
        const val REDIS_KEY_TTL_SEC = 60 // 장애 복구 시간을 고려한 시간으로 설정해야함.
    }

    @Bean
    fun redisLockRegistry() = RedisLockRegistry(
        redisConnectionFactory,
        REGISTRY_PREFIX,
        REDIS_KEY_TTL_SEC * 1000L
    ).also {
        it.setRedisLockType(RedisLockRegistry.RedisLockType.PUB_SUB_LOCK) // spring docs 에 따라 PUB_SUB_LOCK 으로 설정
    }
}
