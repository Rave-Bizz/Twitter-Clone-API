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
    val username: String = "",
    val password: String = "",
    @OneToMany(fetch = FetchType.LAZY)
    val posts: MutableList<Post> = mutableListOf(),
    @ManyToMany(fetch = FetchType.EAGER)
    val roles: MutableList<Role> = mutableListOf(),
)
