package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.DTOs.userDTO.UserCreateDTO;
import br.edu.ifpb.veritas.DTOs.userDTO.UserDTO;
import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserCreateDTO dto) {
        // Adicionar validação para e-mail duplicado
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email já cadastrado.");
        }

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setRole(dto.getRole());
        newUser.setRegistration(dto.getRegistration());

        User savedUser = userRepository.save(newUser);
        return new UserDTO(savedUser);
    }

    public List<UserDTO> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserDTO> listUsersByRole(UserRole role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}
