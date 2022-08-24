package com.twitter.clone.twitterclone.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Date

@Slf4j
class CustomAuthenticationFilter(
    private val authManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {

    private val log: Logger = LoggerFactory.getLogger(CustomAuthenticationFilter::class.java)

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val username = request?.getParameter("username")
        val password = request?.getParameter("password")
        log.info("Username is: $username : Password is: $password")
        val authenticationToken = UsernamePasswordAuthenticationToken(username, password)
        log.info("authenticationToken is : ${authenticationToken.isAuthenticated}")
        return authManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val user: User = authResult?.principal as User
        val algo = Algorithm.HMAC256("secret".toByteArray())
        val access_token =
            JWT.create()
                .withSubject(user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 600 * 60 * 1000))
                .withIssuer(request?.requestURL.toString())
                .withClaim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algo)
        val refresh_token =
            JWT.create()
                .withSubject(user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 6000 * 60 * 100))
                .withIssuer(request?.requestURL.toString())
                .withClaim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algo)
//        response?.setHeader("access_token", access_token)
//        response?.setHeader("refresh_token", refresh_token)
        log.info("access token is: $access_token : refresh token is: $refresh_token")
        val tokens: HashMap<String, String> = hashMapOf()
        tokens["access_token"] = access_token
        tokens["refresh_token"] = refresh_token
        response?.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response?.outputStream, tokens)
    }
}