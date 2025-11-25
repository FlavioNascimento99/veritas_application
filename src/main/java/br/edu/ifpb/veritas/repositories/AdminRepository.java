package br.edu.ifpb.veritas.repositories;

import java.util.List;
import java.util.Optional;

import br.edu.ifpb.veritas.models.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;

@Repository
public interface AdminRepository extends JpaRepository<Administrator, Long> {
   /**
    * Queries específicas. 
    */

   Optional<Administrator> findByLogin(String login);
   Optional<Administrator> findByRegister(String register);

   // List<User> findByRole(UserRole role);
}
