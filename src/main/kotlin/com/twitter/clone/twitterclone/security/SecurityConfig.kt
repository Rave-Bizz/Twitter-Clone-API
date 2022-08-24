package com.twitter.clone.twitterclone.security

import com.twitter.clone.twitterclone.filter.CustomAuthenticationFilter
import com.twitter.clone.twitterclone.filter.CustomAuthorizationFilter
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val bCrypt: BCryptPasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService)?.passwordEncoder(bCrypt)
    }

    override fun configure(http: HttpSecurity?) {
        val customAuthenticationFilter = CustomAuthenticationFilter(authenticationManager())
        customAuthenticationFilter.setFilterProcessesUrl("/api/login")
        http?.csrf()?.disable()
        http?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http?.authorizeRequests()?.antMatchers("/api/login/**")?.permitAll()
        http?.authorizeRequests()?.antMatchers("/api/refresh/token/**")?.permitAll()
        http?.authorizeRequests()?.antMatchers(GET, "/api/user/**")?.hasAuthority("ROLE_USER")
        http?.authorizeRequests()?.antMatchers(GET, "/api/post/**")?.hasAuthority("ROLE_USER")
        http?.authorizeRequests()?.anyRequest()?.authenticated()
//        http?.authorizeRequests()?.anyRequest()?.permitAll()
        http?.addFilter(customAuthenticationFilter)
        http?.addFilterBefore(CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }
}