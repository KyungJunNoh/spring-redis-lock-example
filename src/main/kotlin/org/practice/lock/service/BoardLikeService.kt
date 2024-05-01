package org.practice.lock.service

import org.practice.lock.entity.Board
import org.practice.lock.repository.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BoardLikeService(
    private val boardRepository: BoardRepository
) {

    fun increase(boardId: Long) {
        val board = getBoardById(boardId)
        board.increaseLike()
    }

    fun decrease(boardId: Long) {
        val board = getBoardById(boardId)
        board.decreaseLike()
    }

    private fun getBoardById(boardId: Long): Board = boardRepository.findById(boardId).orElseThrow {
        throw IllegalArgumentException("not found board id: $boardId")
    }
}
