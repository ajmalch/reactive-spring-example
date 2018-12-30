package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class ReactiveSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringClientApplication.class, args);
    }

    @Bean
    MapReactiveUserDetailsService authentication() {
        UserDetails ajmal = User.withDefaultPasswordEncoder()
                .username("ajmal").password("password").roles("Admin", "User").build();
        UserDetails shadiya = User.withDefaultPasswordEncoder()
                .username("shadiya").password("password").roles("User").build();

        return new MapReactiveUserDetailsService(ajmal, shadiya);
    }

    @Bean
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {

        http.csrf().disable();
        http.httpBasic();
        http.authorizeExchange().pathMatchers("/proxy").authenticated()
                .anyExchange().permitAll();

        return http.build();
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(5, 7);
    }

    @Bean
    RouteLocator gateWay(RouteLocatorBuilder routeLocatorBuilder) {

        return routeLocatorBuilder
                .routes()
                .route(rSpec -> rSpec.host("*.maliha.aqila")
                        .and().path("/proxy")
                        .filters(fSpec -> fSpec.setPath("/users")
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                .requestRateLimiter(config ->
                                        config.setRateLimiter(redisRateLimiter()))
                        )
                        .uri("http://localhost:8080"))
                .route(rSpec -> rSpec.host("*.maliha.aqila")
                        .and().path("/proxyStream")
                        .filters(fSpec -> fSpec.setPath("/usersStream")
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                .requestRateLimiter(config ->
                                        config.setRateLimiter(redisRateLimiter()))
                        )
                        .uri("http://localhost:8080"))
                .
                        build();
    }

}

