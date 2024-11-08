package com.schoolIntranet.controller;

import com.schoolIntranet.persistence.entity.UserEntity;
import com.schoolIntranet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }

    @PutMapping("/unable/{id}")
    public ResponseEntity<String> inhabilitarCuenta(@PathVariable Long id) {
        userService.unableAccount(id);
        return ResponseEntity.ok("Account disabled successfully.");
    }

    @PutMapping("/update")
    public ResponseEntity<String> actualizarDatos(@RequestBody UserEntity userEntityActualizado, @AuthenticationPrincipal String username) {
        userService.updateUserData(userEntityActualizado, username);
        return ResponseEntity.ok("Data updated successfully.");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editarCuenta(@PathVariable Long id, @RequestBody UserEntity userEntityActualizado) {
        userService.editAccount(id, userEntityActualizado);
        return ResponseEntity.ok("Account updated successfully.");
    }
}
