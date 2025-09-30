package com.neurofleetx.service;

import com.neurofleetx.dto.RegisterRequest;
import com.neurofleetx.model.User;
import com.neurofleetx.model.Role;
import com.neurofleetx.repository.UserRepository;
import com.neurofleetx.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            registerRequest.getCompany(),
            passwordEncoder.encode(registerRequest.getPassword()),
            registerRequest.getUserType()
        );

        Set<Role> roles = new HashSet<>();
        
        // Assign roles based on user type
        switch (registerRequest.getUserType().toLowerCase()) {
            case "admin":
                Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);
                break;
            case "fleet_manager":
            case "manager":
                Role managerRole = roleRepository.findByName(Role.ERole.ROLE_MANAGER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(managerRole);
                break;
            case "driver":
                Role driverRole = roleRepository.findByName(Role.ERole.ROLE_DRIVER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(driverRole);
                break;
            default:
                Role customerRole = roleRepository.findByName(Role.ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(customerRole);
        }
        
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateLastLogin(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}