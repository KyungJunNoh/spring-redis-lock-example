package org.practice.lock.service

import org.practice.lock.dto.BoardCreateRequest
import org.practice.lock.repository.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
) {

    fun create(request: BoardCreateRequest) {
        boardRepository.save(request.toEntity())
    }

    @Transactional(readOnly = true)
    fun lookupLike(boardId: Long): Int {
        return boardRepository.findById(boardId)
            .orElseThrow { IllegalStateException("not found board id: $boardId") }
            .like
    }
}
