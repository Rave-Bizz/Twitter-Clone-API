package com.twitter.clone.twitterclone.domain.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
data class Post(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    val content: String = "",
    val createdAt: String = "",
    @OneToMany(fetch = FetchType.EAGER)
    val comments: MutableList<Comment> = mutableListOf(),
    val username: String = "",
)
