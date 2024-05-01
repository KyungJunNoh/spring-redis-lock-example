package org.practice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringIntegrationLockApplication

fun main(args: Array<String>) {
    runApplication<SpringIntegrationLockApplication>(*args)
}
