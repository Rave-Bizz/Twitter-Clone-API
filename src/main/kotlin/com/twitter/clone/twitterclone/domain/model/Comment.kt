package com.twitter.clone.twitterclone.domain.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
data class Comment(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    val content: String = "",
    val createdAt: String = "",
    val postId: Long? = null,
    val username: String = "",
)