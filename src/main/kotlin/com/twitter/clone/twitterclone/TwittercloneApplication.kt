package com.twitter.clone.twitterclone

import com.twitter.clone.twitterclone.domain.model.Comment
import com.twitter.clone.twitterclone.domain.model.Post
import com.twitter.clone.twitterclone.domain.model.Role
import com.twitter.clone.twitterclone.domain.model.User
import com.twitter.clone.twitterclone.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class TwittercloneApplication() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun run(userService: UserService): CommandLineRunner {
        return CommandLineRunner { args ->
            userService.saveUser(
                User(
                    null,
                    "John Travolta",
                    "John",
                    "John",
                    "1234",
                )
            )
            val post = Post(null, "New Post", System.currentTimeMillis().toString(), System.currentTimeMillis().toString(), mutableListOf(),"John")
            val postWithId = userService.savePost(post)
            val comment = Comment(null, "Awesome stuff big guy", System.currentTimeMillis().toString(), System.currentTimeMillis().toString(), postId = postWithId.id, "John")
            userService.addCommentToPost(comment)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TwittercloneApplication>(*args)
}
