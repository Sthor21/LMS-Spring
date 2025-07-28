package com.infosys.services;

import com.infosys.beans.User;
import com.infosys.dto.requests.UserRequestDto;
import com.infosys.dto.response.BookResponseDTO;
import com.infosys.dto.response.UserResponseDTO;
import com.infosys.enums.BorrowStatus;
import com.infosys.enums.Role;
import com.infosys.repository.BorrowRecordRepository;
import com.infosys.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserResponseDTO createUser(UserRequestDto request) {
        // Validate email and phone number uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }

        User user = modelMapper.map(request, User.class);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO updateUser(Integer id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Validate email and phone number uniqueness (excluding current user)
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        userRepository.delete(user);
    }

    public List<UserResponseDTO> getUsersByRole(String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            return userRepository.findByRole(userRole).stream()
                    .map(user -> modelMapper.map(user, UserResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }
    }

    public List<BookResponseDTO> getUserCurrentBooks(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return borrowRecordRepository.findByUser_IdAndStatus(userId, BorrowStatus.ACTIVE)
                .stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord.getBook(), BookResponseDTO.class))
                .collect(Collectors.toList());
    }

    public Long getTotalFineByUserId(Integer userId) {
        long fine = borrowRecordRepository.getTotalFineByUserId(userId);
        return fine;
    }
}
