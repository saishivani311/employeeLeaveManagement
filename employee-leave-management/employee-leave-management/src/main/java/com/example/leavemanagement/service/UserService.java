package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.CreateUserRequest;
import com.example.leavemanagement.entity.Role;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.exception.BadRequestException;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(CreateUserRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new BadRequestException("Email is already registered");
        }

        Role role = request.role() == null ? Role.EMPLOYEE : request.role();
        return userRepository.save(new User(request.name().trim(), request.email().trim().toLowerCase(), role));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
