package com.twitter.clone.twitterclone.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.clone.twitterclone.domain.model.Comment
import com.twitter.clone.twitterclone.domain.model.Post
import com.twitter.clone.twitterclone.domain.model.User
import com.twitter.clone.twitterclone.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class UserController(
    private val userService: UserService
) {
    val log: Logger = LoggerFactory.getLogger(UserController::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleAlreadyExist(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok().body(userService.getUsers())
    }

    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.OK)
    fun getPosts(): List<Post> {
        log.info("about to query all posts")
        return userService.getAllPosts()
    }

    @GetMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.OK)
    fun getCommentForPost(@PathVariable postId: Long): List<Comment> {
//        log.info("attempting query for comments on post $postId")
        return userService.getCommentsForPost(postId)
    }

    @PostMapping("/users/save")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveUser(@RequestBody user: User): User {
        return userService.saveUser(user)
    }

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    fun savePost(@RequestBody post: Post): Post {
        return userService.savePost(post)
    }

    @PatchMapping("/post")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePost(@RequestBody post: Post): Post {
        return userService.updatePost(post = post)
    }

    @PatchMapping("/post/comment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateComment(@RequestBody comment: Comment): Comment {
        return userService.updateComment(comment = comment)
    }

    @DeleteMapping("/post")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePost(@RequestBody post: Post) {
        userService.deletePost(post = post)
    }

//    @DeleteMapping("/post/comment")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    fun deleteComment(@RequestBody comment: Comment) {
//        userService.deleteComment(comment = comment)
//    }

    @PostMapping("/post/comment")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveComment(@RequestBody comment: Comment): Comment {
        return userService.addCommentToPost(comment)
    }

    @GetMapping("/refresh/token")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse) {
        log.info("request is: $request")
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        log.info("auth header is: $authorizationHeader")
        if (authorizationHeader?.startsWith("Bearer ") == true) {
            try {
                val refresh_token = authorizationHeader.substring("Bearer ".length)
                val algo = Algorithm.HMAC256("secret".toByteArray())
                val verifier = JWT.require(algo).build()
                val decodedJwt: DecodedJWT = verifier.verify(refresh_token)
                val username = decodedJwt.subject
                val user = userService.getUser(username)
                val access_token =
                    JWT.create()
                        .withSubject(user.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.requestURL.toString())
                        .withClaim("roles", user.roles.toString())
                        .sign(algo)
                log.info("access token is: $access_token")
                val tokens: HashMap<String, String> = hashMapOf()
                tokens["access_token"] = access_token
                tokens["refresh_token"] = refresh_token
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, tokens)

            } catch (e: Exception) {
                log.error("Error using refresh token in ${e.message}")
                val errors: HashMap<String, String> = hashMapOf()
                errors["error_message"] = e.message ?: "Something went wrong"
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, errors)
            }
        } else {
            throw RuntimeException("Refresh token is missing")
        }
    }
}