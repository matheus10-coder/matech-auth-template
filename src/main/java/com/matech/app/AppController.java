package com.matech.app;

import com.matech.user.User;
import com.matech.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** todo
        - move logic of this controller to a service class
        - create a new user role based access for ADMIN users
        - only users with ADMIN roles can access full body user records
        - maybe create a new table where we will keep the user any security
          related info should be at jwt_security
 */

@RestController
@RequestMapping("/api/v1/app-controller")
public class AppController {
    private final UserRepository userRepository;

    public AppController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello from secured endpoint!!");
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return userRepository.findById(id).stream().map(
                user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    user.setRole(updatedUser.getRole());
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                }
        ).findFirst().orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        return userRepository.findById(id).map(
                user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok("The following user has been deleted: " + user);
                }
        ).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
