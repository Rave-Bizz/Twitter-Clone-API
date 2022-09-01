package com.twitter.clone.twitterclone.service

import com.twitter.clone.twitterclone.domain.model.Comment
import com.twitter.clone.twitterclone.domain.model.Post
import com.twitter.clone.twitterclone.domain.model.Role
import com.twitter.clone.twitterclone.domain.model.User

interface UserService {
    fun saveUser(user: User): User
    fun getAllPostForUser(username: String): List<Post>
    fun getAllPosts(): List<Post>
    fun savePost(post: Post): Post
    fun addCommentToPost(comment: Comment): Comment
    fun updatePost(post: Post): Post
    fun updateComment(comment: Comment): Comment
    fun getCommentsForPost(findByPostId: Long): List<Comment>
    fun getUser(username: String): User
    // want to make this paginated eventually
    fun getUsers(): List<User>
}