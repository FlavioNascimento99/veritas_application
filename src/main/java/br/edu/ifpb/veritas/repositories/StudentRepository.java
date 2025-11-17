package br.edu.ifpb.veritas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.veritas.models.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
   Optional<Student> findByLogin(String login);
   Optional<Student> findByRegister(String register);
}
