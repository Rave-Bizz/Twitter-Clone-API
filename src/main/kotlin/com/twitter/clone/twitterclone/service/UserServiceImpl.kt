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
        val userWithEncodedPassword = user.copy(password = passwordEncoder.encode(user.password))
        val userInDB = userRepo.save(userWithEncodedPassword)
        addRoleToUser(userInDB.username, "ROLE_USER")
        return userInDB
    }

    override fun saveRole(role: Role): Role {
        log.info("Saving new role to the database $role")
        return roleRepo.save(role)
    }

    override fun addRoleToUser(username: String, roleName: String) {
        log.info("Adding role $roleName to user $username")
        val user = userRepo.findByUsername(username)
        val role = roleRepo.findByName(roleName)
        user?.roles?.add(role)
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
        val user = userRepo.findByUsername(post.username)
        val postWithId = postRepo.save(post)
        user?.posts?.add(postWithId)
        return postWithId
    }

    override fun addCommentToPost(comment: Comment): Comment {
        log.info("Saving new comment to the database $comment")
        if (comment.postId == null) throw NoSuchElementException("Missing postId")
        val savedComment = commentRepo.save(comment)
        val post = postRepo.findById(comment.postId).get()
        post.comments.add(savedComment)
        return savedComment
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
        user.roles.forEach { role -> authorities.add(SimpleGrantedAuthority(role.name)) }
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }

}