package br.edu.ifpb.veritas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   /**
    * Queries específicas. 
    */

   Optional<User> findByEmail(String email);

   List<User> findByRole(UserRole role);
}
