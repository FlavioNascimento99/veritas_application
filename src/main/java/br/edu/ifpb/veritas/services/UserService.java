package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new ResourceNotFoundException("Login já cadastrado.");
        }
        return userRepository.save(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public List<User> listUserByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}
