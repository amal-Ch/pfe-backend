package com.example.administration.controller;

import com.example.administration.entity.User;
import com.example.administration.payload.request.SignupRequest;
import com.example.administration.payload.response.MessageResponse;
import com.example.administration.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthController authController;

    @GetMapping("/role-user-counts")
    public List<Map<String, Object>> getRoleUserCounts() {
        return userService.getRoleUserCounts();
    }

    // Create a new user
    @PostMapping("/addUser")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest signUpRequest) {
        return authController.registerUser(signUpRequest);
    }
    @GetMapping("/getAll")
    public ResponseEntity<Page<User>> getUsers(Pageable pageable) {
        Page<User> userPage = userService.getAllUsersPage(pageable);
        return ResponseEntity.ok(userPage);
    }

    // Get user by ID
    @GetMapping("/getbyId/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update user by ID

    @PutMapping("/editUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody SignupRequest updateRequest) {
        try {
            User updatedUser = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    // Delete user by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
