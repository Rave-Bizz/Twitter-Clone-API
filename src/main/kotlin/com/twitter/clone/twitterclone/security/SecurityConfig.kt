package com.twitter.clone.twitterclone.security

import com.twitter.clone.twitterclone.filter.CustomAuthenticationFilter
import com.twitter.clone.twitterclone.filter.CustomAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.GET
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//class SecurityConfig(
//    private val userDetailsService: UserDetailsService,
//    private val bCrypt: BCryptPasswordEncoder
//) : WebSecurityConfigurerAdapter() {
//
//    override fun configure(auth: AuthenticationManagerBuilder?) {
//        auth?.userDetailsService(userDetailsService)?.passwordEncoder(bCrypt)
//    }
//
//    override fun configure(http: HttpSecurity?) {
//        val customAuthenticationFilter = CustomAuthenticationFilter(authenticationManager())
//        customAuthenticationFilter.setFilterProcessesUrl("/api/login")
//        http?.csrf()?.disable()?.cors()
//        http?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        http?.authorizeRequests()?.antMatchers("/api/login/**", "/api/refresh/token/**", "/api/users/save/**")?.permitAll()
////        http?.authorizeRequests()?.antMatchers("/api/refresh/token/**")?.permitAll()
//        http?.authorizeRequests()?.antMatchers(GET, "/api/user/**")?.hasAuthority("ROLE_USER")
////        http?.authorizeRequests()?.antMatchers(GET, "/api/post/**")?.hasAuthority("ROLE_USER")
////        http?.authorizeRequests()?.antMatchers(GET, "/api/post/comment/**")?.hasAuthority("ROLE_USER")
//        http?.authorizeRequests()?.anyRequest()?.authenticated()
////        http?.authorizeRequests()?.anyRequest()?.permitAll()
//        http?.addFilter(customAuthenticationFilter)
//        http?.addFilterBefore(CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)
//    }
//
//    @Bean
//    override fun authenticationManager(): AuthenticationManager {
//        return super.authenticationManager()
//    }
//}

@Configuration
class SecurityConfiguration(
    private val userDetailsService: UserDetailsService,
    private val bCrypt: BCryptPasswordEncoder
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        val customAuthenticationFilter = CustomAuthenticationFilter(authenticationManager)
        customAuthenticationFilter.setFilterProcessesUrl("/api/login")
        http.csrf()?.disable()?.cors()
        http.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.authorizeHttpRequests().requestMatchers("/api/login/**", "/api/refresh/token/**", "/api/users/save/**")
            ?.permitAll()
//        http?.authorizeRequests()?.antMatchers("/api/refresh/token/**")?.permitAll()
        http.authorizeHttpRequests().requestMatchers(GET, "/api/user/**")?.hasAuthority("ROLE_USER")
        http.authorizeHttpRequests().anyRequest().authenticated()
        http.addFilter(customAuthenticationFilter)
        http.addFilterBefore(CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

//    @Bean
//    @Throws(java.lang.Exception::class)
//    fun authManager(
//        http: HttpSecurity,
//    ): AuthenticationManager? {
//        return http.getSharedObject(AuthenticationManagerBuilder::class.java)
//            .userDetailsService(userDetailsService)
//            .passwordEncoder(bCrypt)
//            .and()
//            .build()
//    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Throws(java.lang.Exception::class)
    protected fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(bCrypt)
        provider.setUserDetailsService(userDetailsService)
        return provider
    }
}
