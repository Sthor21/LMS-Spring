package com.infosys.controller;

import com.infosys.dto.requests.UserRequestDto;
import com.infosys.dto.response.BookResponseDTO;
import com.infosys.dto.response.UserResponseDTO;
import com.infosys.services.BorrowService;
import com.infosys.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDto request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @Positive Integer id, @Valid @RequestBody UserRequestDto request) {
        return new ResponseEntity<>(userService.updateUser(id, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable @Positive Integer id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        return new ResponseEntity<>(userService.getUsersByRole(role), HttpStatus.OK);
    }

    @GetMapping("/current-books/{userId}")
    public ResponseEntity<List<BookResponseDTO>> getUserCurrentBooks(@PathVariable @Positive Integer userId) {
        return new ResponseEntity<>(userService.getUserCurrentBooks(userId), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/total-fine")
    public Long getTotalFineByUserId(@PathVariable Integer userId) {
        return userService.getTotalFineByUserId(userId);
    }
}
