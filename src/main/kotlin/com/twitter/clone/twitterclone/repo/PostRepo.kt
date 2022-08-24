package com.twitter.clone.twitterclone.repo

import com.twitter.clone.twitterclone.domain.model.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepo: JpaRepository<Post, Long> {
    fun findByUsername(username: String): List<Post>
}