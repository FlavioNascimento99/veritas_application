package br.edu.ifpb.veritas.configs;

import br.edu.ifpb.veritas.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita o @PreAuthorize
@RequiredArgsConstructor
public class securityConfig {

   private final CustomUserDetailsService customUserDetailsService;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
         .csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/register", "/about", "/contact", "/css/**", "/js/**", "/webjars/**")
            .permitAll()
            .anyRequest()
            .authenticated()
         )
         .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true)
            .permitAll()
         )
         .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
         );
      return http.build();
   }

   @Bean
   public UserDetailsService userDetailsService() {
      return customUserDetailsService;
   }

   /**
    * Bean que define o algoritmo de criptografia de senhas.
    */
   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}
