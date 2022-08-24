package com.twitter.clone.twitterclone.repo

import com.twitter.clone.twitterclone.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}