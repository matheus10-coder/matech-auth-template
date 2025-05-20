package com.matech.auth;

import com.matech.config.JwtService;
import com.matech.user.Role;
import com.matech.user.User;
import com.matech.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Implement the methods register and authenticate
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    // Encode the password - injection
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        //create a user out of the Register Request
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        // Generate a jwtToken for the user
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        // authentication manager will do the work to check if the username or password is not correct and throw an error
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        // to this point the user is authenticated! generate a token and send it back
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
