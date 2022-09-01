package com.twitter.clone.twitterclone.service

import com.twitter.clone.twitterclone.domain.model.Comment
import com.twitter.clone.twitterclone.domain.model.Post
import com.twitter.clone.twitterclone.domain.model.Role
import com.twitter.clone.twitterclone.domain.model.User
import com.twitter.clone.twitterclone.repo.CommentRepo
import com.twitter.clone.twitterclone.repo.PostRepo
import com.twitter.clone.twitterclone.repo.RoleRepo
import com.twitter.clone.twitterclone.repo.UserRepo
import jakarta.transaction.Transactional
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
@Transactional
@Slf4j
class UserServiceImpl(
    private val userRepo: UserRepo,
    private val roleRepo: RoleRepo,
    private val postRepo: PostRepo,
    private val commentRepo: CommentRepo,
    private val passwordEncoder: PasswordEncoder
) : UserService, UserDetailsService {
    private val log: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun saveUser(user: User): User {
        log.info("Saving new user to the database ${user.name}")
        val userWithEncodedPassword = user.copy(password = passwordEncoder.encode(user.password), roles = "ROLE_USER")
        return userRepo.save(userWithEncodedPassword)
    }

    override fun getAllPostForUser(username: String): List<Post> {
        return listOf()
    }

    override fun getAllPosts(): List<Post> {
        log.info("Grabbing all posts")
        return postRepo.findAll()
    }

    override fun savePost(post: Post): Post {
        log.info("Saving new post to the database $post")
        val postWithTime = post.copy(createdAt = System.currentTimeMillis().toString(), updatedAt = System.currentTimeMillis().toString())
        return postRepo.save(postWithTime)
    }

    override fun addCommentToPost(comment: Comment): Comment {
        log.info("Saving new comment to the database $comment")
        if (comment.postId == null) throw NoSuchElementException("Missing postId")
        val savedComment = commentRepo.save(comment)
        val post = postRepo.findById(comment.postId).get()
        post.comments.add(savedComment)
        return savedComment
    }

    override fun updatePost(post: Post): Post {
        if(post.id == null) throw IllegalArgumentException()
        val postFromDb = postRepo.findById(post.id).get()
        log.info("Updating post to the database ${post.id}")
        return postRepo.save(postFromDb.copy(content = post.content, updatedAt = System.currentTimeMillis().toString()))
    }

    override fun deletePost(post: Post) {
        if(post.id == null) throw IllegalArgumentException()
        postRepo.delete(post)
    }

    override fun deleteComment(comment: Comment) {
        if(comment.id == null && comment.postId == null) throw IllegalArgumentException()
        log.info("deleting comment from database ${comment.id}")
//        val post = postRepo.findById(comment.postId!!).get()
//        post.comments.remove(comment)
//        postRepo.save(post)
        commentRepo.delete(comment)
    }

    override fun updateComment(comment: Comment): Comment {
        if(comment.id == null) throw IllegalArgumentException()
        val commentFromDb = commentRepo.findById(comment.id).get()
        log.info("Updating comment to the database ${comment.id}")
        return commentRepo.save(commentFromDb.copy(content = comment.content, updatedAt = System.currentTimeMillis().toString()))
    }

    override fun getCommentsForPost(findByPostId: Long): List<Comment> {
        val comments = commentRepo.findByPostId(findByPostId)
        log.info("Saving new comment to the database ${comments[0]}")
        return comments
    }

    @Throws(UsernameNotFoundException::class)
    override fun getUser(username: String): User {
        log.info("Grabbing user $username from database")
        return userRepo.findByUsername(username = username)
            ?: throw UsernameNotFoundException("User not found in the database")
    }

    override fun getUsers(): List<User> {
        log.info("Grabbing all users")
        return userRepo.findAll()
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user =
            userRepo.findByUsername(username) ?: throw UsernameNotFoundException("User not found in the database")
        log.info("User was found in the database")
        val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        val role = user.roles
        authorities.add(SimpleGrantedAuthority(role))
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }
}