package com.matech.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;

/**
 * This is the first filter on the security layer where we check first for the
 * jwt token and make the flow proceed depending on each answer we have
 */
@Component //Tell spring to be manage equivalent to Bean (IoC)
@RequiredArgsConstructor // will create a construct w/ any private final variable
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    //interface - we will implement our own
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // our request from the client
            @NonNull HttpServletResponse response, // our response from user
            @NonNull FilterChain filterChain // chain responsibility from design patter
    ) throws ServletException, IOException {

        // 1 - check JWT or Bearer Token
        final String authHeader = request.getHeader("Authorization"); // token should be in the header - header name
        final String jwt; //store the token
        final String userEmail;

        // check point - for null or bearer token is not present in the header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // pass to the next filter
            return;
        }

        //extract the token from the header
        jwt = authHeader.substring(7); // count the white space as well
        userEmail = jwtService.extractUsername(jwt);// extract user email from token

        // user not authenticated yet or not connected yet
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //check or get the user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            //check if user(token) is valid or
            if (jwtService.validateToken(jwt, userDetails)) {
                //valid
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // enforce the authentication token with our request
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Add security context holder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response); // hint so the next filter is executed
        }
    }
}
