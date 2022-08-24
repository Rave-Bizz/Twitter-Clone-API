package com.twitter.clone.twitterclone.repo

import com.twitter.clone.twitterclone.domain.model.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepo: JpaRepository<Role, Long> {
    fun findByName(name: String): Role
}