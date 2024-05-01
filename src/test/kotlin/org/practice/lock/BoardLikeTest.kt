package org.practice.lock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.practice.lock.entity.Board
import org.practice.lock.repository.BoardRepository
import org.practice.lock.service.BoardLikeFacade
import org.practice.lock.service.BoardLikeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@ActiveProfiles("test")
@SpringBootTest
class BoardLikeTest @Autowired constructor(
    val boardLikeFacade: BoardLikeFacade,
    val boardLikeService: BoardLikeService,
    val boardRepository: BoardRepository,
){
    companion object {
        const val THREAD_COUNT = 100
    }

    @BeforeEach
    fun setUp() {
        boardRepository.save(Board(name = "게시글"))
    }

    @Test
    @DisplayName("(락 사용 O)게시글 좋아요 증가 정합성 보장 성공 테스트")
    fun increase() {
        executeMultiThread(threadCount = THREAD_COUNT) {
            boardLikeFacade.increase(boardId = 1L)
        }

        val board = boardRepository.findById(1L).get()

        assertThat(board.like).isEqualTo(THREAD_COUNT)
    }

    @Test
    @DisplayName("(락 사용 X)게시글 좋아요 증가 정합성 보장 실패 테스트")
    fun increaseNotUseLock() {
        executeMultiThread(threadCount = THREAD_COUNT) {
            boardLikeService.increase(boardId = 1L)
        }

        val board = boardRepository.findById(1L).get()

        assertThat(board.like).isNotEqualTo(THREAD_COUNT)
    }

    private fun executeMultiThread(threadCount: Int, runnable: () -> Unit) {
        val threadPool = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        for (i in 0 until threadCount) {
            threadPool.submit {
                try {
                    runnable()
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
    }
}
