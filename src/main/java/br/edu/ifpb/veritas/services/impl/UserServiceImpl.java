package br.edu.ifpb.veritas.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.repositories.UserRepository;
import br.edu.ifpb.veritas.services.UserService;
import br.edu.ifpb.veritas.services.exceptions.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User existing = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        // O 'role' é definido pela hierarquia de classes, não deve ser setado aqui.
        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        User existing = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(existing);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByName(username);
    }
}
