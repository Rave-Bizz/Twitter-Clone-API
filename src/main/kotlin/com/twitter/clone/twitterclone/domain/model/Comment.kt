package com.twitter.clone.twitterclone.domain.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="comment")
data class Comment(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    val content: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val postId: Long? = null,
    val username: String = "",
)