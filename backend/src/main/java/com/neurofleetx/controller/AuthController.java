package com.neurofleetx.controller;

import com.neurofleetx.dto.JwtResponse;
import com.neurofleetx.dto.LoginRequest;
import com.neurofleetx.dto.RegisterRequest;
import com.neurofleetx.model.User;
import com.neurofleetx.security.UserDetailsImpl;
import com.neurofleetx.security.JwtUtils;
import com.neurofleetx.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userService.findByEmail(loginRequest.getEmail()).orElseThrow();
        userService.updateLastLogin(loginRequest.getEmail());

        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail(), 
                                               user.getFirstName(), user.getLastName(), user.getCompany(), user.getUserType(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Error: Email is already in use!");
        }

        User user = userService.createUser(signUpRequest);

        return ResponseEntity.ok("User registered successfully!");
    }
}