package org.practice.lock.controller

import org.practice.lock.dto.BoardCreateRequest
import org.practice.lock.service.BoardLikeFacade
import org.practice.lock.service.BoardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BoardController(
    private val boardService: BoardService,
    private val boardLikeFacade: BoardLikeFacade,
) {

    @PostMapping("/boards")
    fun create(@RequestBody request: BoardCreateRequest) {
        boardService.create(request)
    }

    @GetMapping("/boards/{boardId}/likes")
    fun lookupLike(@PathVariable boardId: Long): Int {
        return boardService.lookupLike(boardId)
    }

    @PostMapping("/boards/{boardId}/likes/increase")
    fun increaseLike(@PathVariable boardId: Long) {
        boardLikeFacade.increase(boardId)
    }

    @PostMapping("/boards/{boardId}/likes/decrease")
    fun decreaseLike(@PathVariable boardId: Long) {
        boardLikeFacade.decrease(boardId)
    }
}
