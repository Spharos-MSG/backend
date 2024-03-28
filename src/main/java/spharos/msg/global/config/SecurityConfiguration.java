package spharos.msg.global.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import spharos.msg.global.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtTokenProvider;
    private final AuthenticationProvider authenticationProvider;

    //cors
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            var cors = new org.springframework.web.cors.CorsConfiguration();
            cors.setAllowedOriginPatterns(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(CsrfConfigurer::disable)
            .authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                    // 허용 범위
                    .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/api-docs/**")
                    .permitAll()
                    .requestMatchers("/api/v1/**").permitAll() //테스트용 주석 제거시, 모든 api 호출 가능.
                    .requestMatchers("/api/v1/users/**").permitAll()
                    .requestMatchers("/api/v1/reissue/**").permitAll()
                    .requestMatchers("/example/**").permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .sessionManagement(
                sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(
                authenticationProvider) //등록할때 사용하는 키는 authenticationProvider를 사용
            .addFilterBefore(jwtTokenProvider,
                UsernamePasswordAuthenticationFilter.class); //내가 만든 필터 추가

        return http.build();
    }
}
