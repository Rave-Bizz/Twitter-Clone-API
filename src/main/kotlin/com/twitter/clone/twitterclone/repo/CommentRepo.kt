package com.twitter.clone.twitterclone.repo

import com.twitter.clone.twitterclone.domain.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepo: JpaRepository<Comment, Long> {
    fun findByPostId(postId: Long): List<Comment>
}