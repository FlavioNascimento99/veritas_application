package br.edu.ifpb.veritas.services;

import java.util.List;
import java.util.Optional;
import br.edu.ifpb.veritas.models.User;

public interface UserService {
    User create(User user);
    User update(Long id, User user);
    void delete(Long id);
    User findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
}
