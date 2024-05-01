package org.practice.lock.dto

import org.practice.lock.entity.Board

class BoardCreateRequest(
    val name: String
) {
    fun toEntity(): Board {
        return Board(
            name = name,
        )
    }
}