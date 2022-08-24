package com.twitter.clone.twitterclone.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

@Slf4j
class CustomAuthorizationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val log: Logger = LoggerFactory.getLogger(CustomAuthorizationFilter::class.java)

        if (request.servletPath.equals("/api/login") || request.servletPath.equals("/api/refresh/token")) {
            log.info("Made it in here ${request.servletPath} && ${request.getHeader(AUTHORIZATION)}")
            filterChain.doFilter(request, response)
        } else {
            val authorizationHeader = request.getHeader(AUTHORIZATION)
            log.info("Inside checking authorization $authorizationHeader")
            if (authorizationHeader?.startsWith("Bearer ") == true) {
                try {
                    val token = authorizationHeader.substring("Bearer ".length)
                    val algo = Algorithm.HMAC256("secret".toByteArray())
                    val verifier = JWT.require(algo).build()
                    val decodedJwt: DecodedJWT = verifier.verify(token)
                    val username = decodedJwt.subject
                    val roles = decodedJwt.claims["roles"]
                    val authorities = mutableListOf(SimpleGrantedAuthority(roles.toString()))
//                    val authorities = roles.map { role ->
//                        log.info("Role ${role}")
//                //                        SimpleGrantedAuthority(role)
//                    }
                    val authToken = UsernamePasswordAuthenticationToken(username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authToken
                    log.info("Inside checking authorization $authToken")
                    filterChain.doFilter(request, response)
                } catch (e: Exception) {
                    log.error("Error Logging in ${e.message}")
//                    response.setHeader("error", e.message)
//                    response.sendError(403)
                    val errors: HashMap<String, String> = hashMapOf()
                    errors["error_message"] = e.message ?: "Something went wrong"
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    ObjectMapper().writeValue(response.outputStream, errors)
                }
            } else {
                filterChain.doFilter(request, response)
            }
        }
    }
}