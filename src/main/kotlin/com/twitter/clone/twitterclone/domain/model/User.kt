package com.twitter.clone.twitterclone.domain.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
data class User(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    val name: String = "",
    val avatar: String = "",
    val username: String = "",
    val password: String = "",
    val roles: Role? = null,
)
