package com.Hyunsoo.PickYouth.global.config;

import com.Hyunsoo.PickYouth.global.security.CustomUserDetailsService;
import com.Hyunsoo.PickYouth.global.security.JwtAuthenticationFilter;
import com.Hyunsoo.PickYouth.global.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final JwtTokenProvider jwtTokenProvider;

  public SecurityConfig(
      CustomUserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
    this.userDetailsService = userDetailsService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return new org.springframework.security.authentication.ProviderManager(provider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter jwtFilter =
        new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);

    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/auth/me")
                    .authenticated()
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/subsidies", "/api/subsidies/**")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
