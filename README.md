# spring-redis-lock-example
### 설명
- redis 분산락(Distributed lock)을 이용하여 분산환경으로부터 공유된 자원을 보호하기 위한 예제입니다.
- 예제는 게시판 서비스에서 게시글의 좋아요 count에 대한 정합성을 보장합니다.
- Spring Integration 의 RedisLockRegistry의 분산 락을 Standard 하게 사용할 수 있게끔 구현했습니다. 

### 실행 전 준비
- docker로 redis 실행
```
docker run --name redis -p 6379:6379 -d redis
```

### 테스트
- [BoardLikeTest.kt](src/test/kotlin/org/practice/lock/BoardLikeTest.kt)를 통해 게시글의 좋아요 count 정합성 테스트를 할 수 있습니다.

### 주요 클래스
- [RedisLockerComponent](src/main/kotlin/org/practice/lock/util/RedisLockerComponent.kt)
```kotlin
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
```

- [RedisLocker](src/main/kotlin/org/practice/lock/util/RedisLocker.kt)
```kotlin
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
```

### ⚠️ 참고
**RedisLockRegistryConfig.kt**
```kotlin
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
```
- key가 만료되었다는 것은 대부분 redis 장애와 연관되므로 장애 유연성에 따라 `REDIS_KEY_TTL_SEC` 의 시간을 설정해야함.
- master/replica connection은 pub/sub방식의 `RedisLockType.PUB_SUB_LOCK`을 원활히 지원하지 않으므로, 상황에 따라 RedisLockType을 설정해야함. (Spring Doc 참고)

### 레퍼런스
- [Spring Integration - RedisLockRegistry](https://docs.spring.io/spring-integration/reference/redis.html#redis-lock-registry)
