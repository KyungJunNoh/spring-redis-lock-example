package org.practice.lock.entity

import jakarta.persistence.*

@Entity
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    @Column(name = "likes")
    var like: Int = 0,
) {

    fun increaseLike() {
        ++like
    }

    fun decreaseLike() {
        --like
    }
}
