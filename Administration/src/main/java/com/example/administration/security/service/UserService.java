package com.example.administration.security.service;

import com.example.administration.entity.ERole;
import com.example.administration.entity.Role;
import com.example.administration.entity.User;
import com.example.administration.payload.request.SignupRequest;
import com.example.administration.repository.RoleRepository;
import com.example.administration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    // Get a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update a user by ID

    public User updateUser(Long id, SignupRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found!"));

        // Check for username or email conflict (if changed)
        if (!user.getUsername().equals(updateRequest.getUsername()) &&
                userRepository.existsByUsername(updateRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (!user.getEmail().equals(updateRequest.getEmail()) &&
                userRepository.existsByEmail(updateRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Update user details
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(updateRequest.getPassword()));
        }

        // Update roles
        Set<String> strRoles = updateRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles != null) {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                        break;
                    case "rh":
                        roles.add(roleRepository.findByName(ERole.ROLE_RH)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                        break;
                    case "chef":
                        roles.add(roleRepository.findByName(ERole.ROLE_CHEF)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                        break;
                    default:
                        roles.add(roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                }
            });
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // Delete a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
