package com.luv2code.springboot.cruddemo.security;

import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class DemoSecurityConfig {

    //hardcoding no longer needed
//    @Bean // used here, as this needs to be initialized only once for all requests for entire application
//    public InMemoryUserDetailsManager userDetailsManager(){
//        UserDetails john = User.builder()
//                .username("john")
//                .password("{noop}1234")
//                .roles("EMPLOYEE")
//                .build();
//
//        UserDetails mary = User.builder()
//                .username("mary")
//                .password("{noop}1234")
//                .roles("EMPLOYEE", "MANAGER")
//                .build();
//
//        UserDetails susan = User.builder()
//                .username("susan")
//                .password("{noop}1234")
//                .roles("EMPLOYEE", "MANAGER", "ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(john, mary, susan);
//    }




    //add support for JDBC
//    @Bean
//    public UserDetailsManager userDetailsManager(DataSource dataSource){
//        //DataSource is autoconfigured by Spring Boot, only injected by us.
//
//        //tells spring to use Jdbc authentication with our dataSource :)
//        return new JdbcUserDetailsManager(dataSource);
//        //when spring security looks up dataSource, it looks we already have defined tables, so it picks that data up.
//
//    }


    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        //define query to retrieve user by username
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?");

        //define query to retrieve roles of user by username
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, role from roles where user_id=?"
        );
        //here user_id is passed in by login form.

        return jdbcUserDetailsManager;
    }


    @Bean // used here, as this needs to be initialized only once for all requests for entire application
    public SecurityFilterChain filterC3hain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "api/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "api/employees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/employees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                );
        //use HTTP basic authentication
        http.httpBasic(Customizer.withDefaults());

        //diable CSRF(Cross Site Request Forgery)
        //in general, not required for stateless REST APIs that uses POST, PUT, DELETE, PATCH
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
